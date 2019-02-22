package sk.meldiron.barlywhitelist.libs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import sk.meldiron.barlywhitelist.Main;

import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

public class SQL {
    private static final SQL mysql = new SQL();

    private SQL() {
    }

    public static SQL getInstance() {
        return mysql;
    }


    private static HikariDataSource connectionLocal;
    private static HikariDataSource connectionRemote;

    private static String host, database, username, password;
    private static int port;

    public interface SQLAction {
        void callback(ResultSet res) throws SQLException;
    }

    public interface SQLBoolAction {
        void callback(boolean success);
    }

    public interface SQLStatementAction {
        void callback(PreparedStatement st) throws SQLException;
    }

    public interface SQLResponse {
        void callback(Connection con, PreparedStatement st, ResultSet res);
    }

    public interface SQLBoolResponse {
        void callback(Connection con, PreparedStatement st, Boolean res);
    }

    public void closeConnections() {
        if(connectionLocal != null) {
            connectionLocal.close();
        }

        if(connectionRemote != null) {
            connectionRemote.close();
        }
    }

    public Connection getConnection() throws Exception {
        if(connectionLocal != null && !connectionLocal.isClosed()) {
            return connectionLocal.getConnection();
        } else if(connectionRemote != null && !connectionRemote.isClosed()) {
            return connectionRemote.getConnection();
        } else {
            connect();
            return getConnection(0);
        }
    }

    public Connection getConnection(Integer tryNumber) throws Exception {
        if(tryNumber >= 5) {
            return null;
        }

        if(connectionLocal != null && !connectionLocal.isClosed()) {
            return connectionLocal.getConnection();
        } else if(connectionRemote != null && !connectionRemote.isClosed()) {
            return connectionRemote.getConnection();
        } else {
            connect();
            return getConnection(tryNumber + 1);
        }
    }

    public void connectLite() {
        Main.i.getLogger().info("SQL disabled in config.yml! Using SQLite file");

        try {
            openSqliteConnection();
            initDatabase(false);

            Main.i.getLogger().info("SQLite connection success");
        } catch (Exception eLite) {
            Main.i.getLogger().info("Cant use SQLite file. Stopping plugin.");
            eLite.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.i);
        }
    }

    public void connect() {
        try {
            openMysqlConnection();

            initDatabase(true);
        } catch (Exception EMySql) {
            Main.i.getLogger().info("SQL could not be connected ! Check your config.yml! Using fallback SQLite file");

            try {
                openSqliteConnection();

                initDatabase(false);

                Main.i.getLogger().info("Fallback to SQLite success");
            } catch (Exception eLite) {
                Main.i.getLogger().info("Cant fallback to SQLite file. Stopping plugin.");
                eLite.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(Main.i);
            }

        }
    }

    public void connect(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;

        connect();
    }

    public void exec(String query, SQLResponse cb) {
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(query);
            ResultSet res = st.executeQuery();
            cb.callback(con, st, res);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void exec(String query, SQLStatementAction statementAction, SQLResponse cb) {
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(query);
            statementAction.callback(st);
            ResultSet res = st.executeQuery();
            cb.callback(con, st, res);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void execAsync(String query) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                exec(query, (con, st, res) -> {
                    closeAll(con, st, res);
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void execAsync(String query, SQLStatementAction statementAction) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                exec(query, statementAction, (con, st, res) -> {
                    closeAll(con, st, res);
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void execAsync(String query, SQLAction action) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                exec(query, (con, st, res) -> {
                    Bukkit.getScheduler().runTask(Main.i, () -> {
                        try {
                            action.callback(res);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        closeAll(con, st, res);
                    });
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void execAsync(String query, SQLStatementAction statementAction, SQLAction action) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                exec(query, statementAction, (con, st, res) -> {
                    Bukkit.getScheduler().runTask(Main.i, () -> {
                        try {
                            action.callback(res);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        closeAll(con, st, res);
                    });
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public static void closeAll(Connection con, PreparedStatement st, ResultSet res) {
        try {
            st.close();
            res.close();
            con.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void closeAll(Connection con, PreparedStatement st) {
        try {
            st.close();
            con.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void run(String query) {
        run(query, (con, st, res) -> {
            closeAll(con, st);
        });
    }

    public void run(String query, SQLBoolResponse cb) {
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(query);
            Boolean wasSuccess = st.execute();
            cb.callback(con, st, wasSuccess);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void run(String query, SQLStatementAction statementAction) {
        run(query, statementAction, (con, st, res) -> {
            closeAll(con, st);
        });
    }

    public void run(String query, SQLStatementAction statementAction, SQLBoolResponse cb) {
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(query);
            statementAction.callback(st);
            Boolean wasSuccess = st.execute();
            cb.callback(con, st, wasSuccess);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void runAsync(String query) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                run(query, (con, st, wasSuccess) -> {
                    closeAll(con, st);
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void runAsync(String query, SQLStatementAction statementAction) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                run(query, statementAction, (con, st, wasSuccess) -> {
                    closeAll(con, st);
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void runAsync(String query, SQLBoolAction action) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                run(query, (con, st, wasSuccess) -> {
                    Bukkit.getScheduler().runTask(Main.i, () -> {
                        try {
                            action.callback(wasSuccess);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        closeAll(con, st);
                    });
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void runAsync(String query, SQLStatementAction statementAction, SQLBoolAction action) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                Bukkit.getScheduler().runTask(Main.i, () -> {
                    run(query, statementAction, (con, st, wasSuccess) -> {
                        try {
                            action.callback(wasSuccess);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        closeAll(con, st);
                    });
                });
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public void promiseAll() {

    }

    private void openSqliteConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + Main.i.getDataFolder().getAbsolutePath() + "/database.db");
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(60000);

        connectionLocal = new HikariDataSource(config);
        connectionLocal.setMaximumPoolSize(10);
    }

    private void openMysqlConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(60000);
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        connectionRemote = new HikariDataSource(config);
        connectionRemote.setMaximumPoolSize(10);

    }

    private void initDatabase(boolean usingRemote) {
        String query = getInitQuery(usingRemote);
        if(query != null && query != "") {
            try {
                String[] queries = query.split(";");

                for(String queryToExec : queries) {
                    run(queryToExec, (con, st, wasSuccess) -> {
                        closeAll(con, st);
                    });
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    private String getInitQuery(boolean usingRemote) {
        try {
            InputStreamReader readStream = new InputStreamReader(Main.i.getResource("init.sql"), "UTF8");

            Scanner s = new Scanner(readStream).useDelimiter("\\A");
            String query = s.hasNext() ? s.next() : "";

            if(usingRemote == false) {
                query = query.replace("AUTO_INCREMENT", "AUTOINCREMENT");
                query = query.replace("INT", "INTEGER");
            }

            return query;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
