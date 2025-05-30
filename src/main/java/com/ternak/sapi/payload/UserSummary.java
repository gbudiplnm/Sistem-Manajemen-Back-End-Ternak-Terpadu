package com.ternak.sapi.payload;

import com.ternak.sapi.security.UserPrincipal;

public class UserSummary {
    private String id;
    private String username;
    private String name;
    private String role;
    private String description;
    private String email;
    private String avatar;
    
    public UserSummary(UserPrincipal user) {
        UserSummary userSummary = new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getRole().equalsIgnoreCase("1") ? "ROLE_ADMINISTRATOR" : user.getRole().equalsIgnoreCase("2") ? "ROLE_PETUGAS" : "ROLE_PETERNAK", "", "");
        this.id = userSummary.getId();
        this.username = userSummary.getUsername();
        this.name = userSummary.getName();
        this.role = userSummary.getRole();
        this.description = userSummary.getDescription();
        this.email = userSummary.getEmail();
        this.avatar = userSummary.getAvatar();
    }

    public UserSummary(String id, String username, String name,String email, String role, String description, String avatar) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.description = description;
        this.avatar = avatar;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
