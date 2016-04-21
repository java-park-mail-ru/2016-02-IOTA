package ru.cdecl.pub.iota.models.game;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class Wildcard implements CardDeckItem {

    @Nullable
    private Set<Card> substituteCards = null;

    @Nullable
    public Set<Card> getSubstituteCards() {
        return substituteCards;
    }

    public void setSubstituteCards(@Nullable Set<Card> substituteCards) {
        this.substituteCards = substituteCards;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
