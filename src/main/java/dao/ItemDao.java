package dao;

import domain.Item;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;


// ToDo implement DAO with JOOQ
public class ItemDao {

    private Map<Long, Item> items = new HashMap<Long, Item>() {{
        put(1L, Item.builder()
                .id(1L)
                .data("First item!")
                .build()
        );
        put(2L, Item.builder()
                .id(2L)
                .data("Second item!")
                .build()
        );
        put(3L, Item.builder()
                .id(3L)
                .data("Third item!")
                .build()
        );
    }};

    public Observable<Item> findById(Long id) {
        if (items.containsKey(id)) {
            return Observable.just(items.get(id));
        }

        return Observable.empty();
    }

    public Observable<Item> findAll() {
        return Observable.from(items.values());
    }

    public Observable<Item> create(Observable<Item> item) {
        return item
                .map(it -> {
                    long lastId = items.keySet().stream()
                            .max(Long::compare)
                            .orElse(0L);

                    long currentId = lastId + 1;

                    Item itemWithId = setItemId(it, currentId);

                    items.put(currentId, itemWithId);

                    return itemWithId;
                });
    }


    public Observable<Item> update(Long id, Observable<Item> item) {
        Observable<Item> itemFromDb = findById(id);
        return Observable.zip(itemFromDb, item, (itFromDb, it) -> {

            Item updatedItem = itFromDb.toBuilder()
                    .title(it.getTitle())
                    .data(it.getData())
                    .build();

            items.put(id, updatedItem);

            return updatedItem;
        });
    }

    public void delete(Long id) {
        items.remove(id);
    }

    private Item setItemId(Item it, long id) {
        return it.toBuilder()
                .id(id)
                .build();
    }

}
