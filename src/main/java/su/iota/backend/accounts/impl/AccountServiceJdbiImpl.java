package su.iota.backend.accounts.impl;

import co.paralleluniverse.fibers.jdbi.FiberDBI;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.Rank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.BooleanMapper;
import org.skife.jdbi.v2.util.LongMapper;
import su.iota.backend.accounts.AccountService;
import su.iota.backend.accounts.exceptions.UserAlreadyExistsException;
import su.iota.backend.accounts.exceptions.UserNotFoundException;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Rank(-100)
@Service
@Immediate
public class AccountServiceJdbiImpl implements AccountService {

    private final FiberDBI dbi;

    @Inject
    public AccountServiceJdbiImpl(DataSource dataSource) {
        dbi = new FiberDBI(dataSource);
        setupTables();
    }

    @Override
    public long createUser(@NotNull UserProfile userProfile) throws UserAlreadyExistsException {
        final String userLogin = userProfile.getLogin();
        final Long insertedUserId;
        try (Handle handle = dbi.open()) {
            if (AccountServiceJdbiImpl.this.isUserExistent(userLogin)) {
                throw new UserAlreadyExistsException();
            }
            handle.execute("insert into user (login, email, password) values (?, ?, ?)",
                    userLogin,
                    userProfile.getEmail(),
                    userProfile.getPassword());
            insertedUserId = handle.createQuery("select last_insert_id()").map(LongMapper.FIRST).first();
        }
        if (insertedUserId == null) {
            throw new AssertionError();
        }
        return insertedUserId;
    }

    @Override
    public void editUser(long userId, @NotNull UserProfile newUserProfile) throws UserNotFoundException, UserAlreadyExistsException {
        if (!isUserExistent(userId)) {
            throw new UserNotFoundException();
        }
        final String newUserLogin = newUserProfile.getLogin();
        if (isUserExistent(newUserLogin)) {
            throw new UserAlreadyExistsException();
        }
        try (Handle handle = dbi.open()) {
            handle.execute("update user set login = ?, email = ?, password = ? where id = ?",
                    newUserProfile.getLogin(),
                    newUserProfile.getEmail(),
                    newUserProfile.getPassword(),
                    userId);
        }
    }

    @Override
    public boolean isUserExistent(long userId) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        return isUserExistentWhere(" and id = :userId ", queryParams);
    }

    @Override
    public boolean isUserExistent(@NotNull String userLogin) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userLogin", userLogin);
        return isUserExistentWhere(" and login = :userLogin ", queryParams);
    }

    @Override
    public void deleteUser(long userId) throws UserNotFoundException {
        try (Handle handle = dbi.open()) {
            handle.execute("delete from user where id = ?", userId);
        }
    }

    @Override
    public @Nullable Long getUserId(@NotNull String userLogin) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select id from user where login = :userLogin")
                    .bind("userLogin", userLogin)
                    .map(LongMapper.FIRST)
                    .first();
        }
    }

    @Override
    public @Nullable UserProfile getUserProfile(@NotNull String userLogin) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userLogin", userLogin);
        return getUserProfileWhere(" and login = :userLogin ", queryParams);
    }

    @Override
    public @Nullable UserProfile getUserProfile(long userId) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        return getUserProfileWhere(" and id = :userId ", queryParams);
    }

    private @Nullable UserProfile getUserProfileWhere(@NotNull String andWhereClause, @NotNull Map<String, ?> whereParams) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select id, login, email, birth_date from user where 1 " + andWhereClause)
                    .bindFromMap(whereParams)
                    .map((index, rs, ctx) -> {
                        return new UserProfile(rs.getLong("id"), rs.getString("login"), rs.getString("email"));
                    }).first();
        }
    }

    private boolean isUserExistentWhere(@NotNull String andWhereClause, @NotNull Map<String, ?> whereParams) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select exists( select 1 from user where 1 " + andWhereClause + " )")
                    .bindFromMap(whereParams)
                    .map(BooleanMapper.FIRST)
                    .first();
        }
    }

    @Override
    public boolean isUserPasswordCorrect(long userId, @NotNull String password) throws UserNotFoundException {
        if (!isUserExistent(userId)) {
            throw new UserNotFoundException();
        }
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select exists( select 1 from user where id = :userId and password = :userPassword )")
                    .bind("userId", userId)
                    .bind("userPassword", String.valueOf(password))
                    .map(BooleanMapper.FIRST)
                    .first();
        }
    }

    private void setupTables() {
        try (Handle handle = dbi.open()) {
            handle.execute("create table if not exists user (id bigint(20) primary key not null auto_increment, " +
                    "login varchar(255) not null, email varchar(255) not null, password varchar(255) not null," +
                    "unique index ix_login (login))");
        }
    }

}
