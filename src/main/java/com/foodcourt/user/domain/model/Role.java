package com.foodcourt.user.domain.model;

/**
 * Account role stored as {@code role.id} in {@code user_db}.
 * Creating an owner always assigns {@link #OWNER}.
 */
public enum Role {
    ADMINISTRATOR((short) 1),
    OWNER((short) 2),
    EMPLOYEE((short) 3),
    CUSTOMER((short) 4);

    private final short id;

    Role(short id) {
        this.id = id;
    }

    public short id() {
        return id;
    }

    public static Role fromId(short id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role id: " + id);
    }
}
