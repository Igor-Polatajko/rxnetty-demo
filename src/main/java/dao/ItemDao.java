package dao;

import domain.Item;
import rx.Observable;

import javax.sql.DataSource;

public class ItemDao {

    private DataSource dataSource;

    public ItemDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Observable<Item> findById(Long id) {
        return null;
    }

    public Observable<Item> findAll() {
        return null;
    }

    public Observable<Item> create(Observable<Item> item) {
        return null;
    }


    public Observable<Item> update(Long id, Observable<Item> item) {
        return null;
    }

    public void delete(Long id) {

    }

}
