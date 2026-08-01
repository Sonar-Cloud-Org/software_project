package edu.najah.software.service;

import edu.najah.software.exception.AuthenticationException;
import edu.najah.software.model.Manager;
import edu.najah.software.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AuthenticationServiceTest {

    private UserRepository userRepository;
    private AuthenticationService authService;
    private Manager testManager;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthenticationService(userRepository);
        testManager = new Manager("admin", "password123");
    }

    @Test
    public void testLoginSuccess() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testManager));

        boolean success = authService.login("admin", "password123");

        assertTrue(success);
        assertTrue(authService.isLoggedIn());
        assertEquals("admin", authService.getCurrentManager().get().getUsername());
    }

    @Test
    public void testLoginFailedInvalidPassword() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testManager));

        boolean success = authService.login("admin", "wrong_password");

        assertFalse(success);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testLoginFailedUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean success = authService.login("unknown", "password123");

        assertFalse(success);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testLogout() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testManager));
        authService.login("admin", "password123");
        assertTrue(authService.isLoggedIn());

        authService.logout();

        assertFalse(authService.isLoggedIn());
        assertFalse(authService.getCurrentManager().isPresent());
    }

    @Test
    public void testCheckLoggedInThrowsException() {
        assertThrows(AuthenticationException.class, () -> {
            authService.checkLoggedIn();
        });
    }

    @Test
    public void testCheckLoggedInSuccess() throws AuthenticationException {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testManager));
        authService.login("admin", "password123");

        assertDoesNotThrow(() -> {
            authService.checkLoggedIn();
        });
    }
}
