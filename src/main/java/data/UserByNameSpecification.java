package data;

public class UserByNameSpecification implements ISQLSpecification {

    private String username;

    public UserByNameSpecification(String username) {
        this.username = username;
    }

    @Override
    public String toSQLQuery() {
        return null;
    }

}
