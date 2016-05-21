package su.iota.backend.messages.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.OutgoingMessage;

public class PlayerActionResultMessage implements OutgoingMessage {

    @Expose
    @SerializedName("__ok")
    private @Nullable Boolean isOk;

    @Expose
    private @Nullable Integer statusCode;

    @Expose
    private @Nullable Object payload;

    public PlayerActionResultMessage() {
    }

    public PlayerActionResultMessage(@Nullable Boolean isOk) {
        this.isOk = isOk;
    }

    public @Nullable Boolean getOk() {
        return isOk;
    }

    public void setOk(@Nullable Boolean ok) {
        isOk = ok;
    }

    public @Nullable Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(@Nullable Integer statusCode) {
        this.statusCode = statusCode;
    }

    public @Nullable Object getPayload() {
        return payload;
    }

    public void setPayload(@Nullable Object payload) {
        this.payload = payload;
    }

}
