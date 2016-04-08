package ru.cdecl.pub.iota.mechanics;

import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GamePlayer {

    Player player;

    public Collection<Card> handCards = new LinkedList<>();

    public GamePlayer(Player player, Collection<Card> handCards) {
        this.player = player;
        this.handCards.clear();
        this.handCards.addAll(handCards);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
