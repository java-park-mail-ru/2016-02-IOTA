package ru.cdecl.pub.iota.mechanics;

import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.CardDeckItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GamePlayer {

    private UserProfile userProfile;

    private Collection<CardDeckItem> handCards = new ArrayList<>();

    public GamePlayer(UserProfile userProfile, Collection<CardDeckItem> handCards) {
        this.userProfile = userProfile;
        this.handCards.clear();
        this.handCards.addAll(handCards);
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Collection<CardDeckItem> getHandCards() {
        return handCards;
    }

    public void setHandCards(Collection<CardDeckItem> handCards) {
        this.handCards = handCards;
    }

    public boolean isConnected() {
        // todo
        return true;
    }

}
