package su.iota.backend.messages.internal;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import org.jetbrains.annotations.NotNull;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.models.UserProfile;

import java.util.Map;

public class GameSessionInitMessage extends RequestMessage<Boolean> implements IncomingMessage {

    private @NotNull Map<ActorRef<Object>, UserProfile> players;

    public GameSessionInitMessage(@NotNull Map<ActorRef<Object>, UserProfile> players) {
        this.players = players;
    }

    public @NotNull Map<ActorRef<Object>, UserProfile> getPlayers() {
        return players;
    }

}
