package su.iota.backend.messages.game.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.game.AbstractPlayerActionMessage;

import java.util.UUID;

public class PlayerPassCardMessage extends AbstractPlayerActionMessage {

    @Nullable
    private String uuid;

    @Nullable
    public UUID getUuid() {
        if (uuid == null) {
            return null;
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static class ResultMessage extends AbstractPlayerActionMessage.AbstractResultMessage {

        public ResultMessage(boolean ok) {
            this.ok = ok;
            if (ok) {
                broadcast = true;
            }
        }
        
    }

}
