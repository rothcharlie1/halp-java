package com.neu.halp.client;

import java.util.Optional;

public interface PetViewer {
    Optional<AdoptablePet> viewNext();

    boolean tryPlay();

    boolean tryAdopt();

    void finished();

}
