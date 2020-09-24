package resource;

import com.google.inject.Inject;
import core.router.request.RequestContext;
import core.router.response.Response;
import dao.ItemDao;
import domain.Item;
import io.netty.handler.codec.http.HttpResponseStatus;
import rx.Observable;

import java.util.List;

public class ItemResource {

    private ItemDao itemDao;

    @Inject
    public ItemResource(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public Response findById(RequestContext requestContext) {

        Observable<Item> item = itemDao.findById(Long.valueOf(requestContext.getPathParam("id")));

        return Response.builder()
                .body(item)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response findAll(RequestContext requestContext) {

        Observable<List<Item>> items = itemDao.findAll().toList();

        return Response.builder()
                .body(items)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response create(RequestContext requestContext) {

        Observable<Item> createdItem = itemDao.create(requestContext.deserializeBody(Item.class));

        return Response.builder()
                .body(createdItem)
                .status(HttpResponseStatus.CREATED)
                .build();
    }

    public Response update(RequestContext requestContext) {

        Observable<Item> updatedItem = itemDao.update(
                Long.valueOf(requestContext.getPathParam("id")),
                requestContext.deserializeBody(Item.class)
        );

        return Response.builder()
                .body(updatedItem)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response delete(RequestContext requestContext) {

        itemDao.delete(Long.valueOf(requestContext.getPathParam("id"))).subscribe();

        return Response.builder()
                .body(Observable.empty())
                .status(HttpResponseStatus.NO_CONTENT)
                .build();
    }

}
