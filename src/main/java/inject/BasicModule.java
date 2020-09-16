package inject;

import com.google.inject.AbstractModule;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import dao.ItemDao;
import handler.HttpHandler;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import resource.ItemResource;
import resource.SimpleTextResource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

            Properties properties = loadProperties();

            ComboPooledDataSource dataSource = new ComboPooledDataSource();

            dataSource.setDriverClass(properties.getProperty("db.driver"));
            dataSource.setJdbcUrl(properties.getProperty("db.url"));
            dataSource.setUser(properties.getProperty("db.user"));
            dataSource.setPassword(properties.getProperty("db.password"));
            dataSource.setMinPoolSize(5);
            dataSource.setMaxPoolSize(20);
            dataSource.setAcquireIncrement(5);

            return dataSource;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadProperties() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("project.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
