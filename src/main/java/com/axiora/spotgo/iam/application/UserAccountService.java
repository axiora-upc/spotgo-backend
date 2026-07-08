package com.axiora.spotgo.iam.application;

import com.axiora.spotgo.iam.domain.model.aggregates.UserAccount;
import com.axiora.spotgo.iam.domain.model.aggregates.PasswordResetCode;
import com.axiora.spotgo.iam.domain.model.valueobjects.UserRole;
import com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories.PasswordResetCodeRepository;
import com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.axiora.spotgo.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.axiora.spotgo.iam.interfaces.rest.resources.UserResource;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAccountService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserAccountRepository userAccountRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final ParkingRepository parkingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final PasswordResetNotificationService passwordResetNotificationService;
    private final Clock clock;
    private final int passwordResetCodeExpirationMinutes;
    private final int passwordResetMaxAttempts;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              PasswordResetCodeRepository passwordResetCodeRepository,
                              ParkingRepository parkingRepository,
                              PasswordEncoder passwordEncoder,
                              JwtTokenService jwtTokenService,
                              PasswordResetNotificationService passwordResetNotificationService,
                              Clock clock,
                              @Value("${app.password-reset.code-expiration-minutes:15}") int passwordResetCodeExpirationMinutes,
                              @Value("${app.password-reset.max-attempts:5}") int passwordResetMaxAttempts) {
        this.userAccountRepository = userAccountRepository;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
        this.parkingRepository = parkingRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.passwordResetNotificationService = passwordResetNotificationService;
        this.clock = clock;
        this.passwordResetCodeExpirationMinutes = passwordResetCodeExpirationMinutes;
        this.passwordResetMaxAttempts = passwordResetMaxAttempts;
    }

    public AuthenticatedUserResource signIn(String email, String password) {
        var user = userAccountRepository.findByEmail(UserAccount.normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.invalid-credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("iam.errors.invalid-credentials");
        }
        return toAuthenticated(user, jwtTokenService.generateToken(user));
    }

    public AuthenticatedUserResource signUpClient(String firstName, String lastName, String email, String password) {
        var normalizedEmail = UserAccount.normalizeEmail(email);
        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("iam.errors.email-taken");
        }
        var user = new UserAccount(firstName, lastName, normalizedEmail, passwordEncoder.encode(password), "", "", UserRole.CLIENT);
        return toAuthenticated(userAccountRepository.save(user), jwtTokenService.generateToken(user));
    }

    public void requestPasswordReset(String email) {
        var normalizedEmail = UserAccount.normalizeEmail(email);
        var user = userAccountRepository.findByEmail(normalizedEmail);
        if (user.isEmpty()) {
            return;
        }

        var rawCode = generateResetCode();
        var now = Instant.now(clock);
        var expiresAt = now.plusSeconds(passwordResetCodeExpirationMinutes * 60L);
        var codeHash = passwordEncoder.encode(rawCode);
        var resetCode = passwordResetCodeRepository.findByEmail(normalizedEmail)
                .map(existing -> {
                    existing.renew(codeHash, expiresAt, now);
                    return existing;
                })
                .orElseGet(() -> new PasswordResetCode(normalizedEmail, codeHash, expiresAt, now));

        passwordResetCodeRepository.save(resetCode);
        passwordResetNotificationService.sendPasswordResetCode(normalizedEmail, rawCode);
    }

    public void confirmPasswordReset(String email, String code, String newPassword) {
        var normalizedEmail = UserAccount.normalizeEmail(email);
        var now = Instant.now(clock);
        var resetCode = passwordResetCodeRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.password-reset.invalid"));

        if (resetCode.isUsed() || resetCode.isExpiredAt(now) || resetCode.getAttempts() >= passwordResetMaxAttempts) {
            throw new IllegalArgumentException("iam.errors.password-reset.invalid");
        }
        if (!passwordEncoder.matches(code, resetCode.getCodeHash())) {
            resetCode.registerFailedAttempt();
            passwordResetCodeRepository.save(resetCode);
            throw new IllegalArgumentException("iam.errors.password-reset.invalid");
        }

        var user = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.password-reset.invalid"));
        user.updatePasswordHash(passwordEncoder.encode(newPassword));
        resetCode.markUsed(now);
        userAccountRepository.save(user);
        passwordResetCodeRepository.save(resetCode);
    }

    public void updatePassword(String userId, String currentPassword, String newPassword) {
        var user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.generic"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("iam.errors.current-password-invalid");
        }
        user.updatePasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
    }

    public List<UserResource> getAllUsers() {
        return userAccountRepository.findAll().stream().map(this::toResource).toList();
    }

    public UserResource getUserById(String userId) {
        return toResource(userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.generic")));
    }

    public UserResource updateUser(String userId, String firstName, String lastName, String phone, String city) {
        var user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.generic"));
        user.updateProfile(firstName, lastName, phone, city);
        return toResource(userAccountRepository.save(user));
    }

    public UserResource toResource(UserAccount user) {
        var parking = user.getRole() == UserRole.ADMIN ? parkingRepository.findByAdminId(user.getId()).orElse(null) : null;
        return new UserResource(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCity(),
                user.getRole().name().toLowerCase(),
                parking != null ? parking.getId() : null,
                parking != null ? parking.getName() : ""
        );
    }

    private AuthenticatedUserResource toAuthenticated(UserAccount user, String token) {
        var resource = toResource(user);
        return new AuthenticatedUserResource(
                resource.id(), resource.firstName(), resource.lastName(), resource.email(),
                resource.phone(), resource.city(), resource.role(), resource.parkingId(), resource.parkingName(), token
        );
    }

    private String generateResetCode() {
        return "%06d".formatted(SECURE_RANDOM.nextInt(1_000_000));
    }
}
