package ru.cdecl.pub.iota.services;

import org.jvnet.hk2.annotations.Contract;
import ru.cdecl.pub.iota.models.UserProfile;

@Contract
public interface AccountService {

    boolean createUser(UserProfile userProfile, char[] password);
    Long getUserId(String userLogin);
    UserProfile getUserProfile(String userLogin);
    UserProfile getUserProfile(Long userId);
    boolean checkUserPassword(Long userId, char[] password);

}
