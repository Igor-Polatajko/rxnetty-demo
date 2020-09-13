package inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import core.mapping.GenericDeserializer;
import core.mapping.GenericSerializer;
import dao.ItemDao;
import handler.HttpHandler;
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
        bind(ObjectMapper.class).toInstance(new ObjectMapper());

        bind(DataSource.class).toInstance(createDataSource());
    }

    private DataSource createDataSource() {

        try {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();

            dataSource.setDriverClass("com.mysql.jdbc.Driver");
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

}
