package com.neu.halp.client;

import java.net.URL;

public interface AdoptablePet {
    String getName();
    int getAge();
    PetType getType();
    boolean getVaccinated();
    URL getPictureUrl();

}
