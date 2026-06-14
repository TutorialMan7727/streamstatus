package uk.vrtxx.streamstatus.common.status;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatusStore {

    private final Map<UUID, EnumSet<Status>> active = new HashMap<>();

    public boolean set(UUID uuid, Status status, boolean enable) {
        EnumSet<Status> set = active.computeIfAbsent(uuid, k ->
            EnumSet.noneOf(Status.class)
        );

        boolean changed = enable ? set.add(status) : set.remove(status);

        if (set.isEmpty()) {
            active.remove(uuid);
        }

        return changed;
    }

    public boolean has(UUID uuid, Status status) {
        EnumSet<Status> set = active.get(uuid);
        return set != null && set.contains(status);
    }

    public boolean hasAny(UUID uuid) {
        EnumSet<Status> set = active.get(uuid);
        return set != null && !set.isEmpty();
    }

    public EnumSet<Status> get(UUID uuid) {
        EnumSet<Status> set = active.get(uuid);
        return set == null ? EnumSet.noneOf(Status.class) : EnumSet.copyOf(set);
    }

    public void clear(UUID uuid) {
        active.remove(uuid);
    }

    public void clearAll() {
        active.clear();
    }
}
