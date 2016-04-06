package ru.cdecl.pub.iota.services;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.jdbc.FiberDataSource;
import co.paralleluniverse.fibers.jdbi.FiberDBI;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.glassfish.hk2.api.Immediate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.BooleanMapper;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.exceptions.base.SecurityPolicyViolationException;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Immediate
public class AccountServiceJdbiImpl implements AccountService {

    private FiberDBI dbi;

    public AccountServiceJdbiImpl() {
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName(DB_NAME);
        mysqlDataSource.setUser(USER_ID);
        mysqlDataSource.setPassword(PASSWORD);

        dbi = new FiberDBI(mysqlDataSource);
    }

    @Override
    public void createUser(@NotNull UserProfile userProfile, char[] password) throws UserAlreadyExistsException, SecurityPolicyViolationException {
        // todo
    }

    @Override
    public void editUser(long userId, @NotNull UserProfile newUserProfile, char[] newPassword) throws UserNotFoundException, UserAlreadyExistsException, SecurityPolicyViolationException {
        // todo
    }

    @Override
    public boolean isUserExistent(long userId) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select exists(select 1 from user where id = :userId)")
                    .bind("userId", userId)
                    .map(BooleanMapper.FIRST)
                    .first();
        }
    }

    @Override
    public void deleteUser(long userId) throws UserNotFoundException {
        try (Handle handle = dbi.open()) {
            handle.execute("delete from user where id = ?", userId);
        }
    }

    @Nullable
    @Override
    @Suspendable
    public Long getUserId(@NotNull String userLogin) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select id from user where login = :userLogin")
                    .bind("userLogin", userLogin)
                    .map(LongMapper.FIRST)
                    .first();
        }
    }

    @Nullable
    @Override
    public UserProfile getUserProfile(@NotNull String userLogin) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userLogin", userLogin);
        return getUserProfileWhere(" and login = :userLogin ", queryParams);
    }

    @Nullable
    @Override
    public UserProfile getUserProfile(long userId) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        return getUserProfileWhere(" and id = :userId ", queryParams);
    }

    private UserProfile getUserProfileWhere(@NotNull String andWhereClause, @NotNull Map<String, ?> whereParams) {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select id, login, email from user where 1 " + andWhereClause)
                    .bindFromMap(whereParams)
                    .map((index, rs, ctx) ->
                            new UserProfile(rs.getLong("id"), rs.getString("login"), rs.getString("email"))
                    ).first();
        }
    }

    @Override
    public boolean isUserPasswordCorrect(long userId, char[] password) throws UserNotFoundException {
        return false;
    }

    public static final String DB_NAME = "iotadb";
    public static final String USER_ID = "root";
    public static final String PASSWORD = "h1w9eyfayl2tn";

}
