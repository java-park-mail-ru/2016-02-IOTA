package ru.cdecl.pub.iota.services;

import org.glassfish.hk2.api.Immediate;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.models.UserProfile;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Immediate
public class AccountServiceMapImpl implements AccountService {

    private AtomicLong userIdGenerator = new AtomicLong(1L);
    private ConcurrentMap<Long, UserProfile> userProfiles = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, char[]> userPasswords = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Long> userIds = new ConcurrentHashMap<>();

    public AccountServiceMapImpl() {
        // throw new RuntimeException();
    }

    @Override
    public boolean createUser(UserProfile userProfile, char[] password) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return false;
        }
        final String userLogin = userProfile.getLogin();
        if (userIds.containsKey(userLogin)) {
            return false;
        }
        final long userId = userIdGenerator.getAndIncrement();
        userIds.put(userLogin, userId);
        userProfiles.put(userId, userProfile);
        userPasswords.put(userId, password);
        return true;
    }

    @Override
    public Long getUserId(String userLogin) {
        return userIds.get(userLogin);
    }

    @Override
    @Nullable
    public UserProfile getUserProfile(String userLogin) {
        final Long userId = userIds.get(userLogin);
        if (userId == null) {
            return null;
        }
        return getUserProfile(userId);
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return userProfiles.get(userId);
    }

    @Override
    public boolean checkUserPassword(Long userId, char[] password) {
        if (!userPasswords.containsKey(userId)) {
            return false;
        }
        final char[] correctPassword = userPasswords.get(userId);
        return Arrays.equals(password, correctPassword);
    }

    public static final int MIN_PASSWORD_LENGTH = 6;

}
