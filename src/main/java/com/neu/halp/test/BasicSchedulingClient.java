package com.neu.halp.test;

import com.neu.halp.client.Appointment;
import com.neu.halp.client.ClinicEntry;

import java.util.Optional;
import java.util.function.Consumer;

public interface BasicSchedulingClient extends Consumer<ClinicEntry> {
  boolean getFinished();
  Optional<Appointment> getResult();
}
