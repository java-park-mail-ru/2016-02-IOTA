package su.iota.backend.messages.game.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import su.iota.backend.messages.OutgoingMessage;

import java.util.UUID;

public class GameStateMessage implements OutgoingMessage {

    @NotNull
    @SerializedName("state")
    @Expose
    private UUID uuid;

    public GameStateMessage(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

}
