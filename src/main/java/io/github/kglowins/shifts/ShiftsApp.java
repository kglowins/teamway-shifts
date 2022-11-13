package io.github.kglowins.shifts;

import static com.google.inject.Guice.createInjector;

import io.github.kglowins.shifts.guicemodules.CommonModule;
import io.github.kglowins.shifts.guicemodules.LocalDevModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShiftsApp {

    public static void main(String[] args) {
        var dependencyInjector = createInjector(new CommonModule(), new LocalDevModule());
    }
}
