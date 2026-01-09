package com.fci.seminar.model;

/**
 * Represents a coordinator who manages seminar operations.
 */
public class Coordinator extends User {
    private static final long serialVersionUID = 1L;

    public Coordinator() {
        super();
        setRole(UserRole.COORDINATOR);
    }

    public Coordinator(String id, String username, String password) {
        super(id, username, password, UserRole.COORDINATOR);
    }
}
