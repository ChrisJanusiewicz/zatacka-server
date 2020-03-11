package data;

import java.util.List;

// TODO: implement specification system which allows for prepared statements to combat SQL injection
public interface IRepository<T> {
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    void remove(ISQLSpecification specification);

    List<T> query(ISQLSpecification specification);
}