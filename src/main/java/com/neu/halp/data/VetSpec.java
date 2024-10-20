package com.neu.halp.data;

import com.google.gson.annotations.SerializedName;

public record VetSpec(
        @SerializedName("name") String name,
        @SerializedName("specialties") String[] specialties,
        @SerializedName("schedule") Day[] schedule) {
}
