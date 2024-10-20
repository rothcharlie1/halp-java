package com.neu.halp.client;

public interface ClientNotificationReceiver {
  void playTimeout();
  void appointmentTimeout();
  void scheduleAnAppointment(AppointmentScheduler scheduler);

  String concerns();
}
