package service;

import com.google.inject.Inject;
import core.mapping.GenericDeserializer;
import core.mapping.GenericSerializer;
import dao.ItemDao;
import domain.Item;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import rx.Observable;

public class ItemService {

    private ItemDao itemDao;

    private GenericSerializer serializer;

    private GenericDeserializer deserializer;

    @Inject
    public ItemService(ItemDao itemDao, GenericSerializer serializer, GenericDeserializer deserializer) {
        this.itemDao = itemDao;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public Observable<Void> findById(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        return response
                .setStatus(HttpResponseStatus.OK)
                .addHeader("Content-Type", "application/json")
                .writeString(
                        serializer.serialize(
                                itemDao.findById(fetchLongIdFromQueryParams(request))
                        ));
    }

    public Observable<Void> findAll(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        return response
                .setStatus(HttpResponseStatus.OK)
                .addHeader("Content-Type", "application/json")
                .writeString(
                        serializer.serialize(
                                itemDao.findAll()
                        ));
    }

    public Observable<Void> create(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        Observable<Item> item = deserializer.deserialize(request.getContent(), Item.class);

        return response
                .setStatus(HttpResponseStatus.CREATED)
                .addHeader("Content-Type", "application/json")
                .writeString(
                        serializer.serialize(
                                itemDao.create(item)
                        ));
    }

    public Observable<Void> update(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        Observable<Item> item = deserializer.deserialize(request.getContent(), Item.class);

        return response
                .setStatus(HttpResponseStatus.OK)
                .addHeader("Content-Type", "application/json")
                .writeString(
                        serializer.serialize(
                                itemDao.update(fetchLongIdFromQueryParams(request), item)
                        ));
    }

    public Observable<Void> delete(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        itemDao.delete(fetchLongIdFromQueryParams(request));

        return response
                .setStatus(HttpResponseStatus.NO_CONTENT)
                .addHeader("Content-Type", "application/json");
    }

    private Long fetchLongIdFromQueryParams(HttpServerRequest<ByteBuf> request) {
        String idStringValue = request.getQueryParameters().get("id").get(0);
        return Long.valueOf(idStringValue);
    }

}
