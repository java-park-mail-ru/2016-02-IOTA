package su.iota.backend.messages.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.FromMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;

public class PlayerActionMessage implements IncomingMessage, FromMessage {

    @Expose
    private @Nullable Boolean ready;

    private ActorRef<?> from;

    @Override
    public ActorRef<?> getFrom() {
        return from;
    }

    public void setFrom(ActorRef<?> from) {
        this.from = from;
    }

    public @Nullable Boolean getReady() {
        return ready;
    }

    public void setReady(@Nullable Boolean ready) {
        this.ready = ready;
    }

    public static class ResultMessage implements OutgoingMessage {

        @Expose
        @SerializedName("__ok")
        private @Nullable Boolean isOk;

        @Expose
        private boolean broadcast = false;

        public boolean isBroadcastTrigger() {
            return broadcast;
        }

        public void setBroadcastTrigger(boolean broadcast) {
            this.broadcast = broadcast;
        }

        public @Nullable Boolean getOk() {
            return isOk;
        }

        public void setOk(@Nullable Boolean ok) {
            isOk = ok;
        }

    }

}
