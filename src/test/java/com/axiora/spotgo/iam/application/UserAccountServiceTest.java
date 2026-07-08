package com.axiora.spotgo.iam.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.axiora.spotgo.iam.domain.model.aggregates.PasswordResetCode;
import com.axiora.spotgo.iam.domain.model.aggregates.UserAccount;
import com.axiora.spotgo.iam.domain.model.valueobjects.UserRole;
import com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories.PasswordResetCodeRepository;
import com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordResetCodeRepository passwordResetCodeRepository;
    @Mock
    private ParkingRepository parkingRepository;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private PasswordResetNotificationService passwordResetNotificationService;

    private BCryptPasswordEncoder passwordEncoder;
    private Clock clock;
    private UserAccountService service;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        clock = Clock.fixed(Instant.parse("2026-07-07T10:15:30Z"), ZoneOffset.UTC);
        service = new UserAccountService(
                userAccountRepository,
                passwordResetCodeRepository,
                parkingRepository,
                passwordEncoder,
                jwtTokenService,
                passwordResetNotificationService,
                clock,
                15,
                5);
    }

    @Test
    void requestPasswordResetAvoidsEnumerationForUnknownEmail() {
        when(userAccountRepository.findByEmail("missing@spotgo.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.requestPasswordReset("missing@spotgo.com"));

        verify(passwordResetCodeRepository, never()).save(any());
        verify(passwordResetNotificationService, never()).sendPasswordResetCode(any(), any());
    }

    @Test
    void requestPasswordResetPersistsCodeAndSendsEmailForKnownUser() {
        var user = new UserAccount("Ana", "Diaz", "client@spotgo.com", passwordEncoder.encode("Password123!"), "", "", UserRole.CLIENT);
        when(userAccountRepository.findByEmail("client@spotgo.com")).thenReturn(Optional.of(user));
        when(passwordResetCodeRepository.findByEmail("client@spotgo.com")).thenReturn(Optional.empty());

        service.requestPasswordReset("client@spotgo.com");

        var codeCaptor = ArgumentCaptor.forClass(PasswordResetCode.class);
        verify(passwordResetCodeRepository).save(codeCaptor.capture());
        verify(passwordResetNotificationService).sendPasswordResetCode(any(), any());
        var saved = codeCaptor.getValue();
        assertEquals("client@spotgo.com", saved.getEmail());
        assertEquals(0, saved.getAttempts());
        assertEquals(Instant.parse("2026-07-07T10:30:30Z"), saved.getExpiresAt());
    }

    @Test
    void confirmPasswordResetRegistersFailedAttemptForInvalidCode() {
        var resetCode = new PasswordResetCode("client@spotgo.com", passwordEncoder.encode("123456"), Instant.parse("2026-07-07T10:30:30Z"), Instant.parse("2026-07-07T10:15:30Z"));
        when(passwordResetCodeRepository.findByEmail("client@spotgo.com")).thenReturn(Optional.of(resetCode));

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.confirmPasswordReset("client@spotgo.com", "654321", "Password123!"));

        assertEquals("iam.errors.password-reset.invalid", exception.getMessage());
        assertEquals(1, resetCode.getAttempts());
        verify(passwordResetCodeRepository).save(resetCode);
    }

    @Test
    void confirmPasswordResetUpdatesPasswordAndMarksCodeUsed() {
        var user = new UserAccount("Ana", "Diaz", "client@spotgo.com", passwordEncoder.encode("OldPassword123!"), "", "", UserRole.CLIENT);
        var resetCode = new PasswordResetCode("client@spotgo.com", passwordEncoder.encode("123456"), Instant.parse("2026-07-07T10:30:30Z"), Instant.parse("2026-07-07T10:15:30Z"));
        when(passwordResetCodeRepository.findByEmail("client@spotgo.com")).thenReturn(Optional.of(resetCode));
        when(userAccountRepository.findByEmail("client@spotgo.com")).thenReturn(Optional.of(user));

        service.confirmPasswordReset("client@spotgo.com", "123456", "NewPassword123!");

        verify(userAccountRepository).save(user);
        verify(passwordResetCodeRepository).save(resetCode);
        assertEquals(true, passwordEncoder.matches("NewPassword123!", user.getPasswordHash()));
        assertEquals(Instant.parse("2026-07-07T10:15:30Z"), resetCode.getUsedAt());
    }
}
