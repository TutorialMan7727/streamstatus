package uk.vrtxx.streamstatus.common.listener;

import java.util.UUID;

public interface StatusLifecycleHandler {
    void onJoin(UUID uuid, String name);

    void onQuit(UUID uuid);
}
