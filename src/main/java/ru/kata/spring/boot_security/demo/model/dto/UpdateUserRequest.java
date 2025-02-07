package ru.kata.spring.boot_security.demo.model.dto;

import ru.kata.spring.boot_security.demo.model.entity.User;

import java.util.Arrays;
import java.util.Objects;

public class UpdateUserRequest {
    private User user;
    private Long[] roleIds;

    public Long getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdateUserRequest that)) return false;
        return Objects.equals(getUser(), that.getUser()) && Objects.deepEquals(getRoleIds(), that.getRoleIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), Arrays.hashCode(getRoleIds()));
    }

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "user=" + user +
                ", roleIds=" + Arrays.toString(roleIds) +
                '}';
    }

}
