package com.neu.halp.data;

import com.google.gson.annotations.SerializedName;
import com.neu.halp.client.PetType;

import java.net.URL;
import java.util.Comparator;
import java.util.function.Predicate;

public class Animal {
    @SerializedName("name") private String name;
    @SerializedName("age") private int age;
    @SerializedName("type") private PetType type;
    @SerializedName("beenVaccinated") private boolean beenVaccinated;
    @SerializedName("picture") private URL picture;

    public Animal(String name, int age, PetType type, boolean beenVaccinated, URL picture) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.beenVaccinated = beenVaccinated;
        this.picture = picture;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    public PetType type() {
        return type;
    }

    public boolean beenVaccinated() {
        return beenVaccinated;
    }

    public void vaccinate() {
        beenVaccinated = true;
    }

    public URL picture() {
        return picture;
    }

    public static Predicate<Animal> isPetTypePredicate(PetType targetPetType) {
        return animal -> animal.type() == targetPetType;
    }

    public static Comparator<Animal> ageAndNameComparator() {
        return Comparator.comparingInt(Animal::age).thenComparing(Comparator.comparing(Animal::name).reversed());
    }
}
