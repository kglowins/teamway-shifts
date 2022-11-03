package io.github.kglowins.shifts.guicemodules;

import com.google.inject.AbstractModule;
import io.github.kglowins.shifts.controllers.VersionInfoController;

public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VersionInfoController.class).asEagerSingleton();
    }

}
