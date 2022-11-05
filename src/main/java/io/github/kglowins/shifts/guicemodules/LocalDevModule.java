package io.github.kglowins.shifts.guicemodules;


import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import io.github.kglowins.shifts.services.H2Runner;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LocalDevModule extends AbstractModule {

    @Override
    protected void configure() {}

    @Inject
    @Provides
    public DataSource provideDataSource(H2Runner h2Runner) {
        log.info("Providing local/dev data source (h2)");
        return h2Runner.getDataSource();
    }
}
