package edu.najah.software.service;

import edu.najah.software.exception.AuthenticationException;
import edu.najah.software.model.Manager;
import edu.najah.software.repository.UserRepository;

import java.util.Optional;


public class AuthenticationService {

    
    private final UserRepository userRepository;

    
    private Manager loggedInManager;

    
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.loggedInManager = null;
    }

    
    public boolean login(String username, String password) {
        Optional<Manager> managerOpt = userRepository.findByUsername(username);
        if (managerOpt.isPresent()) {
            Manager manager = managerOpt.get();
            if (manager.getPassword().equals(password)) {
                loggedInManager = manager;
                return true;
            }
        }
        return false;
    }

    
    public void logout() {
        loggedInManager = null;
    }

    
    public boolean isLoggedIn() {
        return loggedInManager != null;
    }

    
    public Optional<Manager> getCurrentManager() {
        return Optional.ofNullable(loggedInManager);
    }

    
    public void checkLoggedIn() throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("Access denied: You must be logged in as a Manager to perform this action.");
        }
    }
}
