package com.neu.halp.test;

import com.neu.halp.client.AdoptablePet;
import com.neu.halp.client.Appointment;
import com.neu.halp.client.ShelterEntry;

import java.util.Optional;
import java.util.function.Consumer;

public interface TestClient extends Consumer<ShelterEntry> {
  boolean getFinished();
  Optional<AdoptablePet> getResult();
  Optional<Appointment> getAppointment();
}
