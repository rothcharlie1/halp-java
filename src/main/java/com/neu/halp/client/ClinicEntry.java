package com.neu.halp.client;

import java.util.Map;

public interface ClinicEntry {
  AppointmentScheduler requestAppointment(AdoptablePet pet);

  AppointmentScheduler requestAppointment(AdoptablePet pet, ClientNotificationReceiver clientNotificationReceiver);

  boolean clientCheckIn(AdoptablePet patient, ClientNotificationReceiver clientNotificationReceiver) throws InterruptedException;

  Map<Appointment, AppointmentResult> appointmentReport();
}
