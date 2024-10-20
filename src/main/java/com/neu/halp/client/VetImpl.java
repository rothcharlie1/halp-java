package com.neu.halp.client;

import java.util.Arrays;
import java.util.Collection;

import com.neu.halp.data.VetSpec;

public class VetImpl implements Vet {
    private final VetSpec vetSpec;

    public VetImpl(VetSpec vetSpec) {
        this.vetSpec = vetSpec;
    }

    @Override
    public String getName() {
        return vetSpec.name();
    }

    @Override
    public Collection<String> getSpecialties() {
        return Arrays.asList(vetSpec.specialties());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final VetImpl other = (VetImpl) obj;
        return this.vetSpec == other.vetSpec;
    }

    @Override
    public int hashCode() {
        return this.vetSpec.hashCode();
    }

}
