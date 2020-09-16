package inject;

import com.google.inject.AbstractModule;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import core.serde.JsonDeserializer;
import dao.ItemDao;
import handler.HttpHandler;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import resource.ItemResource;
import resource.SimpleTextResource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpHandler.class);
        bind(ItemResource.class);
        bind(ItemDao.class);
        bind(SimpleTextResource.class);

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

}
