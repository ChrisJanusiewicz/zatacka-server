package config;

import java.io.*;
import java.time.Duration;
import java.util.Properties;

public class ProjectProperties implements IDBProperties, IServerProperties, IMatchmakingProperties, IGameProperties, ICryptoProperties {

    private static ProjectProperties instance;
    private static String PROPERTIES_PATH = "res/zatacka.properties";

    private final Properties prop;

    private ProjectProperties() {

        prop = new Properties();

        loadProperties();

    }

    public static synchronized ProjectProperties getInstance() {
        if (instance == null) {
            instance = new ProjectProperties();
        }
        return instance;
    }

    private void saveProperties() {
        try (OutputStream output = new FileOutputStream(PROPERTIES_PATH)) {

            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void loadDefaultProperties() {

        prop.setProperty("db-url", "jdbc:sqlite:res/test.db");

        prop.setProperty("login-token-length", "256");
        prop.setProperty("max-username-length", "32");
        prop.setProperty("login-token-duration", "30");
        prop.setProperty("login-server-port", "1025");
        prop.setProperty("game-server-port", "1024");

        prop.setProperty("max-lobby-size", "8");

        prop.setProperty("server-tick-rate", "60");
        prop.setProperty("map-empty", "254");
        prop.setProperty("map-wall", "255");
        prop.setProperty("map-width", "800");
        prop.setProperty("map-height", "540");
        prop.setProperty("password-hash-iterations", "256");
        prop.setProperty("password-key-length", "256");

        prop.setProperty("keystore-location", "res/keystore.jks");
    }

    private void loadProperties() {
        File f = new File(PROPERTIES_PATH);

        if (f.exists() && f.isFile()) {
            try (InputStream input = new FileInputStream(PROPERTIES_PATH)) {

                prop.load(input);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            loadDefaultProperties();
            saveProperties();
        }
    }


    @Override
    public String get_DB_URL() {
        return prop.getProperty("db-url");
    }

    @Override
    public String get_DB_PASS() {
        return prop.getProperty("db-password");
    }

    @Override
    public int get_LOGIN_TOKEN_LENGTH() {
        return Integer.parseInt(prop.getProperty("login-token-length"));
    }

    @Override
    public int get_MAX_USERNAME_LENGTH() {
        return Integer.parseInt(prop.getProperty("max-username-length"));
    }

    @Override
    public Duration get_LOGIN_TOKEN_DURATION() {
        return Duration.ofSeconds(Integer.parseInt(prop.getProperty("login-token-duration")));
    }

    @Override
    public int get_LOGIN_SERVER_PORT() {
        return Integer.parseInt(prop.getProperty("login-server-port"));
    }

    @Override
    public int get_GAME_SERVER_PORT() {
        return Integer.parseInt(prop.getProperty("game-server-port"));
    }

    @Override
    public int get_MAX_LOBBY_SIZE() {
        return Integer.parseInt(prop.getProperty("max-lobby-size"));
    }

    @Override
    public int get_SERVER_TICK_RATE() {
        return Integer.parseInt(prop.getProperty("server-tick-rate"));
    }

    @Override
    public byte get_MAP_EMPTY() {
        return Byte.parseByte(prop.getProperty("map-empty"));
    }

    @Override
    public byte get_MAP_WALL() {
        return Byte.parseByte(prop.getProperty("map-wall"));
    }

    @Override
    public int get_MAP_WIDTH() {
        return Integer.parseInt(prop.getProperty("map-width"));
    }

    @Override
    public int get_MAP_HEIGHT() {
        return Integer.parseInt(prop.getProperty("map-height"));
    }

    @Override
    public int get_PASSWORD_HASH_ITERATIONS() {
        return Integer.parseInt(prop.getProperty("password-hash-iterations"));
    }

    @Override
    public int get_PASSWORD_KEY_LENGTH() {
        return Integer.parseInt(prop.getProperty("password-key-length"));
    }

    @Override
    public String get_KEYSTORE_LOCATION() {
        return prop.getProperty("keystore-location");
    }
}
