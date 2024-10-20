package com.neu.halp.client;

import java.util.concurrent.locks.ReentrantLock;

public class AdoptablePetState {
    protected AdoptablePet pet;
    protected ReentrantLock lock;
    protected boolean isAdopted;

    protected AdoptablePetState(AdoptablePet pet) {
        this.pet = pet;
        this.lock = new ReentrantLock();
        this.isAdopted = false;
    }
}
