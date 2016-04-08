package ru.cdecl.pub.iota.models.game;

import ru.cdecl.pub.iota.models.UserProfile;

public class Player {

    // todo: выкинуть класс

    UserProfile userProfile;
    public volatile Long sessionId; // todo: выкинуть к чертям, это ващё чёто с чем-то

    public Player(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
