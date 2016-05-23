package su.iota.backend.messages.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.OutgoingMessage;

public class GameStateMessage implements OutgoingMessage {

    @Expose
    @SerializedName("__ok")
    private @Nullable Boolean isOk;

    @Expose
    private @Nullable Object payload;

    public GameStateMessage() {
    }

    public GameStateMessage(@Nullable Boolean isOk) {
        this.isOk = isOk;
    }

    public @Nullable Boolean getOk() {
        return isOk;
    }

    public void setOk(@Nullable Boolean ok) {
        isOk = ok;
    }

    public @Nullable Object getPayload() {
        return payload;
    }

    public void setPayload(@Nullable Object payload) {
        this.payload = payload;
    }

}
