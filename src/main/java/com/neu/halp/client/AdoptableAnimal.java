package com.neu.halp.client;

import com.neu.halp.data.Animal;

import java.net.URL;

public class AdoptableAnimal implements AdoptablePet {
    private Animal animal;

    public AdoptableAnimal(Animal animal) {
        this.animal = animal;
    }

    @Override
    public String getName() {
        return animal.name();
    }

    @Override
    public int getAge() {
        return animal.age();
    }

    @Override
    public PetType getType() {
        return animal.type();
    }

    @Override
    public boolean getVaccinated() {
        return animal.beenVaccinated();
    }

    @Override
    public URL getPictureUrl() {
        return animal.picture();
    }

    public void vaccinate() {
        animal.vaccinate();
    }
}
