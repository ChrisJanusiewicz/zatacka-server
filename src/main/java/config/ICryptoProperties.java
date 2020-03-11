package config;

public interface ICryptoProperties {
    public int get_PASSWORD_HASH_ITERATIONS();

    public int get_PASSWORD_KEY_LENGTH();

    public String get_KEYSTORE_LOCATION();
}
