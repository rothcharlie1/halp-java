package com.neu.halp.data;

import com.google.gson.annotations.SerializedName;

public enum Day {
    @SerializedName("M")
    MONDAY,

    @SerializedName("Tu")
    TUESDAY,

    @SerializedName("W")
    WEDNESDAY,

    @SerializedName("Th")
    THURSDAY,

    @SerializedName("F")
    FRIDAY,

    @SerializedName("Sa")
    SATURDAY,

    @SerializedName("Su")
    SUNDAY
}
