package io.github.kglowins.shifts;

import static com.google.inject.Guice.createInjector;

import io.github.kglowins.shifts.guicemodules.ControllersModule;
import io.github.kglowins.shifts.guicemodules.LocalDevModule;
import io.github.kglowins.shifts.guicemodules.UtilModule;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import pl.coffeepower.guiceliquibase.GuiceLiquibaseModule;

@Slf4j
public class ShiftsApp {

    public static void main(String[] args) {
        var dependencyInjector = createInjector(
            new ControllersModule(),
            new UtilModule(),
            /* TODO:liquibase please ignore
                I left Liquibase code intentionally to have an example in the future
            new GuiceLiquibaseModule(),
            */
            new LocalDevModule());
    }
}
