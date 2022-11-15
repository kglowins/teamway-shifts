package io.github.kglowins.shifts;

import static com.google.inject.Guice.createInjector;

import io.github.kglowins.shifts.guicemodules.ControllersModule;
import io.github.kglowins.shifts.guicemodules.LocalDevModule;
import io.github.kglowins.shifts.guicemodules.UtilModule;
import lombok.extern.slf4j.Slf4j;
import pl.coffeepower.guiceliquibase.GuiceLiquibaseModule;

@Slf4j
public class ShiftsApp {

    public static void main(String[] args) {
        var dependencyInjector = createInjector(
            new ControllersModule(),
            new UtilModule(),
            new GuiceLiquibaseModule(),
            new LocalDevModule());
    }
}
