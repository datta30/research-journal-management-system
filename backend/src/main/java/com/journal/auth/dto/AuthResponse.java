package com.journal.auth.dto;

import com.journal.user.dto.UserSummary;

public record AuthResponse(String token, String refreshToken, UserSummary user) {
}
