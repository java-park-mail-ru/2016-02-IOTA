package ru.cdecl.pub.iota.mechanics;

import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.Player;

import java.util.ArrayList;
import java.util.List;

public class GamePlayer {

    Player player;

    public GamePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
