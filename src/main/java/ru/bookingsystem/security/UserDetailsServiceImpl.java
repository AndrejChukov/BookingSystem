package ru.bookingsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bookingsystem.repository.UserRepository;

/**
 * Loads application user details from the persistent store (database).
 *
 * <p>This class is a simple adapter between Spring Security's {@link UserDetailsService}
 * contract and the application's {@link ru.bookingsystem.repository.UserRepository}.
 *
 * <p>Responsibilities:
 * - Fetch a user by username from {@link UserRepository}.
 * - Return an object that implements {@link UserDetails} for Spring Security to use
 *   during authentication and authorization.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Locates the user based on the username.
     *
     * <p>In the event the user cannot be found, a {@link UsernameNotFoundException} is thrown
     * which signals Spring Security that authentication should fail.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated {@link UserDetails} record (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or is not valid for authentication
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
