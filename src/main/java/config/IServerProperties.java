package config;

import java.time.Duration;

public interface IServerProperties {
    public int get_LOGIN_SERVER_PORT();

    public int get_GAME_SERVER_PORT();

    public int get_LOGIN_TOKEN_LENGTH();

    public int get_MAX_USERNAME_LENGTH();

    public Duration get_LOGIN_TOKEN_DURATION();
}
