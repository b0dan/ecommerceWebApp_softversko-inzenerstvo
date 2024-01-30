package com.fict.eCommerceWebApp.configs;

import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<UserEntity> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<UserEntity> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        } else {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity) {
                return Optional.of((UserEntity) principal);
            } else if (principal instanceof Long id) {
                return userRepository.findById(id);
            } else if (principal instanceof String username) {
                return userRepository.findByUsername(username);
            } else {
                return Optional.empty();
            }
        }
    }
}