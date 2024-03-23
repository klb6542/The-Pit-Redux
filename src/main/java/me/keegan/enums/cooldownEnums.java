package me.keegan.enums;

/*
 * Copyright (c) 2024. Created by klb.
 */

public enum cooldownEnums {
    /*
     * NORMAL - cooldown ends after duration and cannot be restarted
     * OVERRIDE - cooldown ens after duration but can be restarted mid-countdown
     * RESET_HIT_COUNTER - similar to override, but resets the hit counter after cooldown ends
     */
    NORMAL,
    OVERRIDE,
    RESET_HIT_COUNTER
}
