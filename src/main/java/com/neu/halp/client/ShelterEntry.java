package com.neu.halp.client;

import java.io.Reader;

public interface ShelterEntry {
  PetViewer viewAnimals();
  PetViewer viewAnimals(ClientNotificationReceiver clientNotificationReceiver);

  void importAnimals(Reader reader);
}
