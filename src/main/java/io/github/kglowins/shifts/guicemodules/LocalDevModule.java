package io.github.kglowins.shifts.guicemodules;


import static com.google.inject.name.Names.named;
import static io.github.kglowins.shifts.enums.Environment.LOCAL_DEV;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.enums.Environment;
import io.github.kglowins.shifts.services.H2Runner;
import io.github.kglowins.shifts.services.identity.IdentityService;
import io.github.kglowins.shifts.services.identity.MockIdentityService;
import javax.inject.Inject;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import pl.coffeepower.guiceliquibase.GuiceLiquibaseConfig;
import pl.coffeepower.guiceliquibase.LiquibaseConfig;
import pl.coffeepower.guiceliquibase.annotation.GuiceLiquibaseConfiguration;


@Slf4j
public class LocalDevModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IdentityService.class).to(MockIdentityService.class);
        bind(Environment.class).toInstance(LOCAL_DEV);
        bind(String.class).annotatedWith(named("localhost")).toInstance("http://localhost:4567");
    }

    @Provides
    public DataSource provideDataSource(H2Runner h2Runner) {
        log.info("Providing local/dev data source (h2)");
        return h2Runner.getDataSource();
    }

    @GuiceLiquibaseConfiguration
    @Provides
    @Inject
    private GuiceLiquibaseConfig createLiquibaseConfig(DataSource dataSource) {
        return GuiceLiquibaseConfig.Builder
            .of(LiquibaseConfig.Builder.of(dataSource)
                .withChangeLogPath("liquibase-changelog.xml")
                .build())
            .build();
    }
}
