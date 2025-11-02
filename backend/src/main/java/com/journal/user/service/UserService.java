package com.journal.user.service;

import com.journal.common.exception.DuplicateResourceException;
import com.journal.common.exception.ResourceNotFoundException;
import com.journal.user.dto.UserSummary;
import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import com.journal.user.repository.UserAccountRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email %s not found".formatted(email)));
    }

    public UserAccount getById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id %s not found".formatted(id)));
    }

    public boolean existsByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public List<UserSummary> listByRole(RoleType role) {
        return userAccountRepository.findByRole(role).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserAccount create(UserAccount user) {
        if (existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        return userAccountRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(UserAccount user) {
        user.setLastLoginAt(OffsetDateTime.now());
        userAccountRepository.save(user);
    }

    public UserSummary toSummary(UserAccount account) {
        return new UserSummary(account.getId(), account.getFullName(), account.getEmail(), account.getRole(), account.getOrganization());
    }
}
