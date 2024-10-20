package com.neu.halp.data;

import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.neu.halp.client.PetType;

import java.util.*;
import java.util.function.Predicate;

public record Configuration(
        @SerializedName("animals") Animal[] animals,
        @SerializedName("clinicians") VetSpec[] clinicians) {

    public static Configuration fromJsonElement(JsonElement json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Configuration.class);
    }

    public Optional<String> oldestAnimal(PetType type) {
        Predicate<Animal> animalFilter = Animal.isPetTypePredicate(type);
        Optional<Animal> oldestPet = Arrays.stream(this.animals)
                .filter(animalFilter)
                .max(Animal.ageAndNameComparator());
        return oldestPet.map(Animal::name);
    }

    public Day mostStaffedDay() {
        Integer[] counts = {0,0,0,0,0,0,0};
        for (VetSpec vet : clinicians) {
            for (Day day : vet.schedule()) {
                counts[day.ordinal()]++;
            }
        }
        List<Integer> iList = Arrays.asList(counts);
        int dayIndex = iList.indexOf(Collections.max(iList));

        return Day.values()[dayIndex];
    }

    public Seniors computeSeniors() {
        String[] animals = {
                oldestAnimal(PetType.Dog).orElse(null),
                oldestAnimal(PetType.Cat).orElse(null)
        };
        return new Seniors(animals, mostStaffedDay());
    }
}
