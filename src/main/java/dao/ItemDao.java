package dao;

import com.google.inject.Inject;
import domain.Item;
import org.jooq.DSLContext;
import rx.Completable;
import rx.Observable;

import java.time.LocalDateTime;

import static org.jooq.sources.tables.Items.ITEMS_;

public class ItemDao {

    private DSLContext dsl;

    @Inject
    public ItemDao(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Observable<Item> findById(Long id) {
        return Observable.just(
                dsl.select()
                        .from(ITEMS_)
                        .where(ITEMS_.ID.eq(id))
                        .fetchAnyInto(Item.class));
    }

    public Observable<Item> findAll() {
        return Observable.from(
                dsl.select()
                        .from(ITEMS_)
                        .fetchInto(Item.class));

    }

    public Observable<Item> create(Observable<Item> item) {

        LocalDateTime now = LocalDateTime.now();

        return item.map(it ->
                dsl.insertInto(ITEMS_, ITEMS_.TITLE, ITEMS_.DATA, ITEMS_.UPDATED_DATE, ITEMS_.CREATED_DATE)
                        .values(it.getTitle(), it.getData(), now, now)
                        .returning()
                        .fetchOne()
                        .into(Item.class));
    }


    public Observable<Item> update(Long id, Observable<Item> item) {
        return item.map(it -> {

            LocalDateTime updatedDate = LocalDateTime.now();

            dsl.update(ITEMS_)
                    .set(ITEMS_.TITLE, it.getTitle())
                    .set(ITEMS_.DATA, it.getData())
                    .set(ITEMS_.UPDATED_DATE, updatedDate)
                    .where(ITEMS_.ID.eq(id))
                    .execute();

            return it.toBuilder()
                    .id(id)
                    .updatedDate(updatedDate)
                    .build();
        });
    }

    public Completable delete(Long id) {

        return Completable.create(subscriber -> {
                    dsl.delete(ITEMS_).where(ITEMS_.ID.eq(id)).execute();
                    subscriber.onCompleted();
                }
        );
    }

}
