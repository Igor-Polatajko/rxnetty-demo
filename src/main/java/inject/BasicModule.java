package inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import core.serde.GenericDeserializer;
import core.serde.GenericSerializer;
import dao.ItemDao;
import handler.HttpHandler;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import service.ItemService;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpHandler.class);
        bind(ItemService.class);
        bind(ItemDao.class);
        bind(GenericSerializer.class);
        bind(GenericDeserializer.class);
        bind(ObjectMapper.class).toInstance(createObjectMapper());

        bind(DSLContext.class).toInstance(createDSLContext());
    }

    private DSLContext createDSLContext() {
        return DSL.using(createDataSource(), SQLDialect.MYSQL);
    }

    private DataSource createDataSource() {

        try {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();

            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
            dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/items?serverTimezone=UTC");
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setMinPoolSize(5);
            dataSource.setMaxPoolSize(20);
            dataSource.setAcquireIncrement(5);

            return dataSource;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}
