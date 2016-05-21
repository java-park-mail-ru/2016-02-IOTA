package su.iota.backend.messages.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.FromMessage;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.messages.IncomingMessage;

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
    
}
