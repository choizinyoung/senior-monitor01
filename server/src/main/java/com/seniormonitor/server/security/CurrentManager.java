package com.seniormonitor.server.security;

import java.time.LocalDateTime;

public record CurrentManager(Long id, String username, String role, String city, String gu, String dong,
                              String jti, LocalDateTime expiresAt) {

    public boolean isMaster() {
        return "MASTER".equals(role);
    }
}
