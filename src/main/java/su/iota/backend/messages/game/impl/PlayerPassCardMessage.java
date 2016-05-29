package su.iota.backend.messages.game.impl;

import org.jetbrains.annotations.NotNull;
import su.iota.backend.messages.game.AbstractPlayerActionMessage;

import java.util.UUID;

public class PlayerPassCardMessage extends AbstractPlayerActionMessage {

    private UUID uuid;

    @NotNull
    @Override
    public String toString() {
        return "PlayerPassCardMessage{" +
                "uuid=" + uuid +
                '}';
    }

    public static class ResultMessage extends AbstractPlayerActionMessage.AbstractResultMessage {
    }

}
