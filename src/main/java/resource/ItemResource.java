package resource;

import com.google.inject.Inject;
import core.router.request.RequestContext;
import core.router.response.Response;
import dao.ItemDao;
import domain.Item;
import exception.NotFoundException;
import io.netty.handler.codec.http.HttpResponseStatus;
import rx.Observable;

import java.util.List;

import static java.util.Objects.isNull;

public class ItemResource {

    private ItemDao itemDao;

    @Inject
    public ItemResource(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public Response findById(RequestContext requestContext) {

        return Response.builder()
                .body(findById(getIdPathParam(requestContext)))
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

        Observable<Item> updatedItem = findById(getIdPathParam(requestContext))
                .flatMap(it -> itemDao.update(
                        it.getId(),
                        requestContext.deserializeBody(Item.class)
                ));

        return Response.builder()
                .body(updatedItem)
                .status(HttpResponseStatus.OK)
                .build();
    }

    public Response delete(RequestContext requestContext) {

        findById(getIdPathParam(requestContext))
                .flatMapCompletable(it -> itemDao.delete(it.getId()))
                .subscribe();

        return Response.builder()
                .body("Item was deleted successfully")
                .status(HttpResponseStatus.NO_CONTENT)
                .build();
    }

    private Long getIdPathParam(RequestContext requestContext) {
        return Long.valueOf(requestContext.getPathParam("id"));
    }

    private Observable<Item> findById(Long id) {
        return itemDao.findById(id)
                .flatMap(it -> {
                    if (isNull(it)) {
                        return Observable.error(new NotFoundException("Item not found"));
                    }
                    return Observable.just(it);
                });
    }

}
