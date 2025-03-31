package com.sumativa1.model;

public class CambioContrasenaRequest {
    private String email;
    private Integer userId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserId() {
        return  userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
