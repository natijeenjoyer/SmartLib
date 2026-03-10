package com.smartlib.repository;

import com.smartlib.model.User;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for users
 */
public class UserRepository {
    private Map<String, User> users;
    private Map<String, User> usersByEmail;

    public UserRepository() {
        this.users = new HashMap<>();
        this.usersByEmail = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail().toLowerCase(), user);
    }

    public User findById(String id) {
        return users.get(id);
    }

    public User findByEmail(String email) {
        return usersByEmail.get(email.toLowerCase());
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<User> searchByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public boolean removeUser(String id) {
        User user = users.remove(id);
        if (user != null) {
            usersByEmail.remove(user.getEmail().toLowerCase());
            return true;
        }
        return false;
    }

    public int getTotalUsers() {
        return users.size();
    }
}
