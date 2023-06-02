package com.carrot.nara.domain;

public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
    
    private String role;

    private UserRole(String role) {
        this.role = role;
    }
    
    public String getRole() {
        return this.role;
    }
}
