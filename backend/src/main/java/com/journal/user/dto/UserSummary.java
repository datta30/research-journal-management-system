package com.journal.user.dto;

import com.journal.user.model.RoleType;

public record UserSummary(Long id, String fullName, String email, RoleType role, String organization) {
}
