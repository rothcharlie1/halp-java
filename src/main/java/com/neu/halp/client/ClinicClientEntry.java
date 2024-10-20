package com.neu.halp.client;

import java.util.function.Consumer;

public interface ClinicClientEntry {
  void connectClient(Consumer<ClinicEntry> client);
}
