package com.neu.halp.client;

import java.util.function.Consumer;

public interface ClientEntry {
  void connectClient(Consumer<ShelterEntry> client);
}
