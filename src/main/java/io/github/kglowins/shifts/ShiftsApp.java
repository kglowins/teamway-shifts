package io.github.kglowins.shifts;

import static com.google.inject.Guice.createInjector;

import com.google.inject.Injector;
import io.github.kglowins.shifts.guicemodules.CommonModule;

public class ShiftsApp {

    public static void main(String[] args) {

        // Guice
        Injector injector = createInjector(new CommonModule());
    }

}
