package io.github.kglowins.shifts.guicemodules;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import io.github.kglowins.shifts.controllers.EmployeeController;
import io.github.kglowins.shifts.controllers.ShiftsController;
import io.github.kglowins.shifts.controllers.VersionInfoController;
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler;
import io.github.kglowins.shifts.services.GsonProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VersionInfoController.class).asEagerSingleton();
        bind(EmployeeController.class).asEagerSingleton();
        bind(ShiftsController.class).asEagerSingleton();
        bind(ErrorHandler.class).asEagerSingleton();
        bind(Gson.class).toInstance(GsonProvider.provide());
    }
}
