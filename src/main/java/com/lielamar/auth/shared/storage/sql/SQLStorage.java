package com.lielamar.auth.shared.storage.sql;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class SQLStorage extends StorageHandler {

    protected HikariDataSource hikari;

    private final String driver;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    private final int maximumPoolSize;
    private final int minimumIdle;
    private final int maximumLifetime;
    private final int keepAliveTime;
    private final int connectionTimeout;

    private final String fullPlayersTableName;

    public SQLStorage(String driver, String host, String database, String username, String password, int port,
                        String tablePrefix, int maximumPoolSize, int minimumIdle, int maximumLifetime, int keepAliveTime, int connectionTimeout) {
        this.driver = driver;

        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port != -1 ? port : 3306;

        this.maximumPoolSize = maximumPoolSize;
        this.minimumIdle = minimumIdle;
        this.maximumLifetime = maximumLifetime;
        this.keepAliveTime = keepAliveTime;
        this.connectionTimeout = connectionTimeout;

        this.fullPlayersTableName = tablePrefix + "players";

        this.setupHikari();
    }


    /**
     * Sets up Hikari
     */
    private void setupHikari() {
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(this.maximumPoolSize);
        hikari.setMinimumIdle(this.minimumIdle);
        hikari.setMaxLifetime(this.maximumLifetime);
        hikari.setKeepaliveTime(this.keepAliveTime);
        hikari.setConnectionTimeout(this.connectionTimeout);

        hikari.setDataSourceClassName(this.driver);
        Properties properties = new Properties();
        properties.setProperty("serverName", host);
        properties.setProperty("port", port + "");
        properties.setProperty("databaseName", database);
        properties.setProperty("user", username);
        if(password.length() > 0)
            properties.setProperty("password", password);
        hikari.setDataSourceProperties(properties);


        this.createTables();
    }

    /**
     * Creates the required tables on the database
     */
    protected void createTables() {
        Connection connection = null;

        try {
            connection = hikari.getConnection();

            String sql = "CREATE TABLE IF NOT EXISTS " + this.fullPlayersTableName + " (`uuid` varchar(64), `key` varchar(64), `ip` varchar(64));";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch(SQLException exception) {
            exception.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }


    @Override
    public String setKey(UUID uuid, String key) {
        Connection connection = null;

        try {
            connection = hikari.getConnection();
            if(connection.isClosed()) return null;

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT `key` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                statement = connection.prepareStatement("UPDATE " + this.fullPlayersTableName + " SET `key` = ? WHERE `uuid` = ?;");
                statement.setString(1, key);
                statement.setString(2, uuid.toString());
            } else {
                statement = connection.prepareStatement("INSERT INTO " + this.fullPlayersTableName + "(`uuid`, `key`, `ip`) VALUES (?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, key);
                statement.setString(3, "");
            }

            statement.executeUpdate();
            return key;
        } catch(SQLException exception) {
            exception.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public String getKey(UUID uuid) {
        Connection connection = null;

        try {
            connection = hikari.getConnection();
            if(connection.isClosed()) return null;

            PreparedStatement statement = connection.prepareStatement("SELECT `key` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String key = result.getString("key");

                return key.equalsIgnoreCase("") ? null : key;
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasKey(UUID uuid) {
        return this.getKey(uuid) != null;
    }

    @Override
    public void removeKey(UUID uuid) {
        this.setKey(uuid, "");
    }


    @Override
    public String setIP(UUID uuid, String ip) {
        Connection connection = null;

        try {
            connection = hikari.getConnection();
            if(connection.isClosed()) return null;

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                statement = connection.prepareStatement("UPDATE " + this.fullPlayersTableName + " SET `ip` = ? WHERE `uuid` = ?;");
                statement.setString(1, ip);
                statement.setString(2, uuid.toString());
            } else {
                statement = connection.prepareStatement("INSERT INTO " + this.fullPlayersTableName + "(`uuid`, `key`, `ip`) VALUES (?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, "");
                statement.setString(3, ip);
            }

            statement.executeUpdate();
            return ip;
        } catch(SQLException exception) {
            exception.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public String getIP(UUID uuid) {
        Connection connection = null;

        try {
            connection = hikari.getConnection();
            if(connection.isClosed()) return null;

            PreparedStatement statement = connection.prepareStatement("SELECT `ip` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String IP = result.getString("ip");
                return IP.equalsIgnoreCase("") ? null : IP;
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasIP(UUID uuid) {
        return this.getIP(uuid) != null;
    }
}