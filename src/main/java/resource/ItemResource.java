package resource;

import com.google.inject.Inject;
import core.rourter.response.Response;
import core.serde.GenericDeserializer;
import dao.ItemDao;
import domain.Item;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.List;

public class ItemResource {

    private ItemDao itemDao;

    private GenericDeserializer deserializer;

    @Inject
    public ItemResource(ItemDao itemDao, GenericDeserializer deserializer) {
        this.itemDao = itemDao;
        this.deserializer = deserializer;
    }

    public Response findById(HttpServerRequest<ByteBuf> request) {

        Observable<Item> item = itemDao.findById(fetchLongIdFromQueryParams(request));

        return Response.builder()
                .body(item)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response findAll(HttpServerRequest<ByteBuf> request) {

        Observable<List<Item>> items = itemDao.findAll().toList();

        return Response.builder()
                .body(items)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response create(HttpServerRequest<ByteBuf> request) {

        Observable<Item> item = deserializer.deserialize(request.getContent(), Item.class);

        Observable<Item> createdItem = itemDao.create(item);

        return Response.builder()
                .body(createdItem)
                .status(HttpResponseStatus.CREATED)
                .build();
    }

    public Response update(HttpServerRequest<ByteBuf> request) {

        Observable<Item> item = deserializer.deserialize(request.getContent(), Item.class);

        Observable<Item> updatedItem = itemDao.update(fetchLongIdFromQueryParams(request), item);

        return Response.builder()
                .body(updatedItem)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response delete(HttpServerRequest<ByteBuf> request) {

        itemDao.delete(fetchLongIdFromQueryParams(request)).subscribe();

        return Response.builder()
                .body(Observable.empty())
                .status(HttpResponseStatus.NO_CONTENT)
                .build();
    }

    private Long fetchLongIdFromQueryParams(HttpServerRequest<ByteBuf> request) {
        String idStringValue = request.getQueryParameters().get("id").get(0);
        return Long.valueOf(idStringValue);
    }

}
