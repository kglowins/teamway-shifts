package io.github.kglowins.shifts.controllers

import com.google.gson.Gson
import com.google.inject.Inject
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.ControllersModule
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.guicemodules.UtilModule
import io.github.kglowins.shifts.helpers.RequestSpecificationProvider
import io.github.kglowins.shifts.repository.Repository
import io.restassured.specification.RequestSpecification
import pl.coffeepower.guiceliquibase.GuiceLiquibaseModule
import spock.guice.UseModules

import javax.inject.Named
import spock.lang.Shared
import spock.lang.Specification


@UseModules([ControllersModule, GuiceLiquibaseModule, UtilModule, LocalDevModule])
class BaseControllerSpec extends Specification {

    @Shared
    @Inject
    @Named("localhost")
    String baseUri

    @Inject
    @Shared
    Gson gson

    @Shared
    RequestSpecification requestSpec = RequestSpecificationProvider.of(gson, baseUri)

    @Shared
    @Inject
    ErrorHandler errorHandler

    @Shared
    @Inject
    Repository repository
}
