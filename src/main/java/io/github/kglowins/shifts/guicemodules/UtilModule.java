package io.github.kglowins.shifts.guicemodules;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import io.github.kglowins.shifts.services.GsonProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).toInstance(GsonProvider.provideConfiguredGson());
    }
}
