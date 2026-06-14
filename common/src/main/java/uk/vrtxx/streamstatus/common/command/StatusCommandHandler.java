package uk.vrtxx.streamstatus.common.command;

import java.util.Locale;
import java.util.UUID;
import uk.vrtxx.streamstatus.common.status.Status;
import uk.vrtxx.streamstatus.common.status.StatusStore;

public class StatusCommandHandler {

    private final StatusStore store;

    public StatusCommandHandler(StatusStore store) {
        this.store = store;
    }

    public Result execute(UUID uuid, String commandName, String[] args) {
        Status status = Status.valueOf(commandName.toUpperCase(Locale.ROOT));

        if (args.length == 0) {
            boolean newState = !store.has(uuid, status);
            store.set(uuid, status, newState);

            return Result.applied(status, newState);
        }

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on" -> {
                boolean changed = store.set(uuid, status, true);
                yield changed
                    ? Result.applied(status, true)
                    : Result.unchanged(status, true);
            }
            case "off" -> {
                boolean changed = store.set(uuid, status, false);
                yield changed
                    ? Result.applied(status, false)
                    : Result.unchanged(status, false);
            }
            default -> Result.invalid(status);
        };
    }

    public record Result(Type type, Status status, boolean state) {
        public enum Type {
            APPLIED,
            UNCHANGED,
            INVALID,
        }

        public static Result applied(Status status, boolean state) {
            return new Result(Type.APPLIED, status, state);
        }

        public static Result unchanged(Status status, boolean state) {
            return new Result(Type.UNCHANGED, status, state);
        }

        public static Result invalid(Status status) {
            return new Result(Type.INVALID, status, false);
        }
    }
}
