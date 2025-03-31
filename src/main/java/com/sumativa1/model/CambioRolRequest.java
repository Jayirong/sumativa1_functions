package com.sumativa1.model;

public class CambioRolRequest {
    private String email;
    private String oldRole;
    private String newRole;
    private Integer userId;

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOldRole() { return oldRole; }
    public void setOldRole(String oldRole) { this.oldRole = oldRole; }
    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
