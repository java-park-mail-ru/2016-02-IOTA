package su.iota.backend.messages.game;

import co.paralleluniverse.actors.ActorRef;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.IncomingMessage;

public class PlayerActionMessage implements IncomingMessage {

    @Expose
    private @Nullable Boolean ready;

    private ActorRef<?> from;

    public @Nullable Boolean getReady() {
        return ready;
    }

    public void setReady(@Nullable Boolean ready) {
        this.ready = ready;
    }

}
