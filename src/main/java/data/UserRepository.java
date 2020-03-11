package data;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class UserRepository implements IRepository<User> {

    Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(User item) {

    }

    @Override
    public void add(Iterable<User> items) {

    }

    public void update(User user) {
        try {
            System.out.println(String.format("Inserting user %s into database", user.getUserName()));
            PreparedStatement ps = conn.prepareStatement("INSERT INTO user (username, password_hash, salt, last_login, registered_at) VALUES (?, ?, ?, ?, ?);");
            ps.setString(1, user.getUserName());
            ps.setBytes(2, user.getPasswordHash());
            ps.setBytes(3, user.getSalt());
            ps.setTimestamp(4, Timestamp.valueOf(user.getLastLogin()));
            ps.setTimestamp(5, Timestamp.valueOf(user.getRegisteredAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(User item) {

    }

    @Override
    public void remove(ISQLSpecification specification) {

    }

    @Override
    public List<User> query(ISQLSpecification specification) {
        return null;
    }

    public User getUserByName(String username) throws SQLException {


        PreparedStatement ps = conn.prepareStatement("SELECT user_id, password_hash, salt, last_login, registered_at FROM user WHERE UPPER(username) = UPPER(?)");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        long id;
        byte[] passwordHash;
        byte[] salt;
        LocalDateTime last_login;
        LocalDateTime registered_at;


        if (rs.next()) {
            id = rs.getLong(1);
            passwordHash = rs.getBytes(2);
            salt = rs.getBytes(3);
            last_login = rs.getTimestamp(4).toLocalDateTime();
            registered_at = rs.getTimestamp(5).toLocalDateTime();

            return new User(id, username, passwordHash, salt, last_login, registered_at);
        }
        return null;

    }

}
