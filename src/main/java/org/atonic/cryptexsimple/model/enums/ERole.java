package org.atonic.cryptexsimple.model.enums;

public enum ERole {
    ROLE_USER("ROLE_USER"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_ADMIN("ROLE_ADMIN");

    public final String value;

    ERole(String value) {
        this.value = value;
    }
}
