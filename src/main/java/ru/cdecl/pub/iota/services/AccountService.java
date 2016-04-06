package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;

@Contract
public interface AccountService {

    void createUser(@NotNull UserProfile userProfile, char[] password) throws UserAlreadyExistsException;

    void editUser(long userId, @NotNull UserProfile newUserProfile, char[] newPassword) throws UserNotFoundException, UserAlreadyExistsException;

    void deleteUser(long userId) throws UserNotFoundException;

    @Nullable
    Long getUserId(@NotNull String userLogin);

    @Nullable
    UserProfile getUserProfile(@NotNull String userLogin);

    @Nullable
    UserProfile getUserProfile(long userId);

    boolean isUserPasswordCorrect(long userId, char[] password) throws UserNotFoundException;

    boolean isUserExistent(long userId);

}
