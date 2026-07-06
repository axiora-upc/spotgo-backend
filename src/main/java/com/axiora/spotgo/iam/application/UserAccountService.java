package com.axiora.spotgo.iam.application;

import com.axiora.spotgo.iam.domain.model.aggregates.UserAccount;
import com.axiora.spotgo.iam.domain.model.valueobjects.UserRole;
import com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.axiora.spotgo.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.axiora.spotgo.iam.interfaces.rest.resources.UserResource;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ParkingRepository parkingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              ParkingRepository parkingRepository,
                              PasswordEncoder passwordEncoder,
                              JwtTokenService jwtTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.parkingRepository = parkingRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
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
        var user = new UserAccount(firstName, lastName, normalizedEmail, passwordEncoder.encode(password), "", "", UserRole.client);
        return toAuthenticated(userAccountRepository.save(user), jwtTokenService.generateToken(user));
    }

    public void resetPassword(String email, String newPassword) {
        var user = userAccountRepository.findByEmail(UserAccount.normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("iam.errors.email-not-found"));
        user.updatePasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
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
        var parking = user.getRole() == UserRole.admin ? parkingRepository.findByAdminId(user.getId()).orElse(null) : null;
        return new UserResource(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCity(),
                user.getRole().name(),
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
}
