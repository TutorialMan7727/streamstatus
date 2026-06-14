package uk.vrtxx.streamstatus.common.listener;

import java.util.UUID;
import uk.vrtxx.streamstatus.common.status.StatusStore;

public class StatusLifecycleService {

    private final StatusStore store;

    public StatusLifecycleService(StatusStore store) {
        this.store = store;
    }

    public boolean hasAny(UUID uuid) {
        return store.hasAny(uuid);
    }

    public void remove(UUID uuid) {
        store.clear(uuid);
    }
}
