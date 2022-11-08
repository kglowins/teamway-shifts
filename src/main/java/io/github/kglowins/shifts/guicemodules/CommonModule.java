package io.github.kglowins.shifts.guicemodules;

import com.google.inject.AbstractModule;
import io.github.kglowins.shifts.controllers.EmployeeController;
import io.github.kglowins.shifts.controllers.ShiftsController;
import io.github.kglowins.shifts.controllers.VersionInfoController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VersionInfoController.class).asEagerSingleton();
        bind(EmployeeController.class).asEagerSingleton();
        bind(ShiftsController.class).asEagerSingleton();
    }
}
