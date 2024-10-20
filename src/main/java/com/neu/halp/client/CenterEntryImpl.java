package com.neu.halp.client;

import com.neu.halp.data.VetSpec;

public class CenterEntryImpl implements CenterEntry {
    private final ShelterEntry shelterEntry;
    private final ClinicEntryImpl clinicEntry;

    public CenterEntryImpl(AdoptablePet[] pets, VetSpec[] vets, int clientIdleTimeoutMilliseconds) {
        var shel = new ShelterEntryImpl(pets, clientIdleTimeoutMilliseconds);
        this.clinicEntry = new ClinicEntryImpl(vets, shel, clientIdleTimeoutMilliseconds);
        shel.registerClinic(clinicEntry);
        this.shelterEntry = shel;
    }

    @Override
    public ShelterEntry getShelterEntry() {
        return this.shelterEntry;
    }

    @Override
    public ClinicEntry getClinicEntry() {
        return this.clinicEntry;
    }
    
}
