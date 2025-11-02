package com.journal.config;

import com.journal.paper.dto.PaperSubmissionRequest;
import com.journal.paper.service.PaperService;
import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import com.journal.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PaperService paperService;

    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder, PaperService paperService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.paperService = paperService;
    }

    @Override
    public void run(String... args) {
        if (!userService.existsByEmail("author@example.com")) {
            userService.create(buildUser("Alice Author", "author@example.com", RoleType.AUTHOR));
        }
        if (!userService.existsByEmail("editor@example.com")) {
            userService.create(buildUser("Evan Editor", "editor@example.com", RoleType.EDITOR));
        }
        if (!userService.existsByEmail("reviewer1@example.com")) {
            userService.create(buildUser("Riley Reviewer", "reviewer1@example.com", RoleType.REVIEWER));
        }
        if (!userService.existsByEmail("reviewer2@example.com")) {
            userService.create(buildUser("Rohan Reviewer", "reviewer2@example.com", RoleType.REVIEWER));
        }

        UserAccount author = userService.getByEmail("author@example.com");
        if (paperService.listForAuthor(author).isEmpty()) {
            paperService.submitPaper(author, new PaperSubmissionRequest(
                    "Quantum Entanglement in Neural Interfaces",
                    "Explores the feasibility of leveraging entanglement for low-latency brain-computer communication.",
                    "quantum computing, neural interfaces",
                    "Quantum entanglement enables correlated states...",
                    "Initial submission.",
                    null));
        }
    }

    private UserAccount buildUser(String name, String email, RoleType role) {
        UserAccount account = new UserAccount();
        account.setFullName(name);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode("Password123!"));
        account.setRole(role);
        return account;
    }
}
