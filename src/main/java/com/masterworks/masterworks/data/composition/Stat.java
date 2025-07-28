package com.masterworks.masterworks.data.composition;

public enum Stat {
    DURABILITY("durability"), DAMAGE("damage"), ACTION_SPEED("action_speed");

    public final String name;

    private Stat(String name) {
        this.name = name;
    }
}
