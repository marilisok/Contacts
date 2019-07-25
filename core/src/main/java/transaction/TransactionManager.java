package transaction;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servlet.FrontController;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransactionManager {
    private static Logger logger = LogManager.getLogger(FrontController.class);
    private static ConcurrentMap<Long, Connection> connectionMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<Long, Savepoint> savepointMap = new ConcurrentHashMap<>();
    private static final DataSource sourseOfData = init();
    public static Connection createConnection() throws SQLException, NamingException{
        return sourseOfData.getConnection();
    }
    private static DataSource init() {
        Properties properties = new Properties();
        try {
            properties.load(TransactionManager.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            logger.error("Error while init database", e);
        }
        MysqlDataSource dataSource = new MysqlDataSource();


        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setUser(properties.getProperty("username"));
        dataSource.setURL(properties.getProperty("contextDataSource"));
        return dataSource;
    }
    public static void startTransaction() throws SQLException, NamingException {
        Connection connection = createConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        Savepoint savepoint = connection.setSavepoint();
        connectionMap.put(Thread.currentThread().getId(), connection);
        savepointMap.put(Thread.currentThread().getId(), savepoint);
    }

    public static void finishTransaction() throws SQLException {
        long treadId = Thread.currentThread().getId();
        savepointMap.remove(treadId);
        Connection c = connectionMap.remove(treadId);

        c.commit();
        closeConnection(c);
    }
    public static void rollbackTransaction() throws SQLException {
        long treadId = Thread.currentThread().getId();
        Savepoint s = savepointMap.remove(treadId);
        Connection c = connectionMap.remove(treadId);
        c.rollback(s);
        closeConnection(c);
    }
    public static void closeConnection(Connection connection){
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error close connection", e);
            }
        }
    }
}
