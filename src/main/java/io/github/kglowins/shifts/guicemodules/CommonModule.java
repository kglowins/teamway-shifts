package io.github.kglowins.shifts.guicemodules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import io.github.kglowins.shifts.controllers.EmployeeController;
import io.github.kglowins.shifts.controllers.VersionInfoController;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VersionInfoController.class).asEagerSingleton();
        bind(EmployeeController.class).asEagerSingleton();
    }
}
