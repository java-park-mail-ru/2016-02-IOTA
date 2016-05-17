package su.iota.backend.messages.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.models.UserProfile;

import java.util.Map;

public class GameSessionInitMessage extends RequestMessage<Boolean> implements IncomingMessage {

    private Map<UserProfile, ActorRef<OutgoingMessage>> players;

    public GameSessionInitMessage(Map<UserProfile, ActorRef<OutgoingMessage>> players) {
        this.players = players;
    }

    public Map<UserProfile, ActorRef<OutgoingMessage>> getPlayers() {
        return players;
    }

}
