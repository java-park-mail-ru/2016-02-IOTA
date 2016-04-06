package ru.cdecl.pub.iota.main;

import co.paralleluniverse.fibers.jdbc.FiberDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.glassfish.hk2.api.Factory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceFactory implements Factory<DataSource> {

    @Override
    public DataSource provide() {
        final DataSource dataSource = setUpDataSource();
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(0)) {
                throw new AssertionError();
            }
            return dataSource;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose(DataSource instance) {
    }

    private DataSource setUpDataSource() {
        final MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setDatabaseName(DB_NAME);
        dataSource.setUser(USER_ID);
        dataSource.setPassword(PASSWORD);

        return FiberDataSource.wrap(dataSource);
    }

    public static final String DB_NAME = "iotadb";
    public static final String USER_ID = "root";
    public static final String PASSWORD = "h1w9eyfayl2tn";

}
