package com.parking.dto;

public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
    private Long userId;

    public AuthResponse(String token, String name, String email, String role, Long userId) {
        this.token  = token;
        this.name   = name;
        this.email  = email;
        this.role   = role;
        this.userId = userId;
    }

    public String getToken()  { return token; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getRole()   { return role; }
    public Long   getUserId() { return userId; }
}
