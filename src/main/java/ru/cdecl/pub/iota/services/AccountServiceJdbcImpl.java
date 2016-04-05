package ru.cdecl.pub.iota.services;

import co.paralleluniverse.fibers.jdbc.FiberDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.glassfish.hk2.api.Immediate;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.sql.DataSource;
import java.sql.*;

@Service
@Immediate
public class AccountServiceJdbcImpl implements AccountService {

    public AccountServiceJdbcImpl() {
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName("iotadb");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("h1w9eyfayl2tn");
        final DataSource dataSource = FiberDataSource.wrap(mysqlDataSource);

        try {
            final Connection conn = dataSource.getConnection();
            conn.createStatement().execute("insert into iotadb.testcommit (name) VALUE ('hello')");
            final boolean notEmpty = conn.createStatement().executeQuery("select * from iotadb.testcommit").next();
            assert (notEmpty);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createUser(UserProfile userProfile, char[] password) {
        return false;
    }

    @Override
    public Long getUserId(String userLogin) {
        return null;
    }

    @Override
    public UserProfile getUserProfile(String userLogin) {
        return null;
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return null;
    }

    @Override
    public boolean checkUserPassword(Long userId, char[] password) {
        return false;
    }
}
