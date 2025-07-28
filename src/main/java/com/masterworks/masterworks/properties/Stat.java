package com.masterworks.masterworks.properties;

public enum Stat {
    DURABILITY("durability"), DAMAGE("damage"), ACTION_SPEED("action_speed");

    public final String name;

    private Stat(String name) {
        this.name = name;
    }
}
