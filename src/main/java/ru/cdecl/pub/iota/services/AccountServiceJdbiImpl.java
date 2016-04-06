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
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.BooleanMapper;
import org.skife.jdbi.v2.util.LongMapper;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    public long createUser(@NotNull UserProfile userProfile, char[] password) throws UserAlreadyExistsException {
        final String userLogin = userProfile.getLogin();
        if (isUserExistent(userLogin)) {
            throw new UserAlreadyExistsException();
        }
        // todo
        return 0L;
    }

    @Override
    public void editUser(long userId, @NotNull UserProfile newUserProfile, char[] newPassword) throws UserNotFoundException, UserAlreadyExistsException {
        if (!isUserExistent(userId)) {
            throw new UserNotFoundException();
        }
        final String newUserLogin = newUserProfile.getLogin();
        if (isUserExistent(newUserLogin)) {
            throw new UserAlreadyExistsException();
        }
        // todo
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
            //noinspection Convert2Lambda,AnonymousInnerClassMayBeStatic
            return handle.createQuery("select id, login, email from user where 1 " + andWhereClause)
                    .bindFromMap(whereParams)
                    .map(new ResultSetMapper<UserProfile>() {
                             @Override
                             public UserProfile map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
                                 return new UserProfile(rs.getLong("id"), rs.getString("login"), rs.getString("email"));
                             }
                         }
                    ).first();
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
    public boolean isUserPasswordCorrect(long userId, char[] password) throws UserNotFoundException {
        // todo
        return false;
    }

    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
        try {
            final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            final PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, keyLength);
            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String DB_NAME = "iotadb";
    public static final String USER_ID = "root";
    public static final String PASSWORD = "h1w9eyfayl2tn";

}
