package io.github.kglowins.shifts;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.inject.Guice.createInjector;
import static spark.Spark.exception;

import io.github.kglowins.shifts.controllers.exceptions.BadRequestException;
import io.github.kglowins.shifts.controllers.exceptions.NotFoundException;
import io.github.kglowins.shifts.guicemodules.CommonModule;
import io.github.kglowins.shifts.guicemodules.LocalDevModule;

public class ShiftsApp {

    public static void main(String[] args) {

        var dependencyInjector = createInjector(new CommonModule(), new LocalDevModule());

        exception(BadRequestException.class, (exception, request, response) -> {
            //log.error("Unexpected error", exception);
            response.status(400);
            response.body(exception.getMessage());
           // if (!DMZ.equals(cluster)) {
             //   response.body(getStackTraceAsString(exception));
          //  }
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            //log.error("Unexpected error", exception);
            response.status(404);
            response.body(exception.getMessage());
            // if (!DMZ.equals(cluster)) {
            //   response.body(getStackTraceAsString(exception));
            //  }
        });

        exception(Exception.class, (exception, request, response) -> {
            //log.error("Unexpected error", exception);
            response.status(500);
            response.body(exception.getMessage());
            // if (!DMZ.equals(cluster)) {
               response.body(getStackTraceAsString(exception));
            //  }
        });
    }

}
