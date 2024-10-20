package com.neu.halp.client;

import java.util.function.Consumer;

public interface CenterClientEntry {
  void connectClient(Consumer<CenterEntry> client);
}
