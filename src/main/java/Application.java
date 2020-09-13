import com.google.inject.Guice;
import com.google.inject.Injector;
import handler.HttpHandler;
import inject.BasicModule;
import org.apache.log4j.BasicConfigurator;

public class Application {

    public static void main(String[] args) {

        // setup basic config for sfl4j
        BasicConfigurator.configure();

        // Run HttpHandler
        Injector injector = Guice.createInjector(new BasicModule());
        injector.getInstance(HttpHandler.class).run();
    }

}
