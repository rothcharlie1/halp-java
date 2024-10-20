package com.neu.halp.data;

import com.google.gson.annotations.SerializedName;

public record Seniors(
        @SerializedName("seniors") String[] seniors,
        @SerializedName("availability") Day availability) {
}
