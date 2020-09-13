package inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import core.mapping.GenericDeserializer;
import core.mapping.GenericSerializer;
import dao.ItemDao;
import handler.HttpHandler;
import service.ItemService;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpHandler.class);
        bind(ItemService.class);
        bind(ItemDao.class);
        bind(GenericSerializer.class);
        bind(GenericDeserializer.class);
        bind(ObjectMapper.class).toInstance(new ObjectMapper());
    }

}
