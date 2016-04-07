package ru.cdecl.pub.iota.main;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.glassfish.hk2.api.Factory;
import ru.cdecl.pub.iota.exceptions.InitializationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceFactory implements Factory<DataSource> {

    @Override
    public DataSource provide() {
        final DataSource dataSource = setUpDataSource();
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(0)) {
                throw new InitializationException();
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

        dataSource.setDatabaseName(System.getProperty("db.name"));
        dataSource.setUser(System.getProperty("db.user"));
        dataSource.setPassword(System.getProperty("db.password"));

        return dataSource;
    }

}
