package io.github.kglowins.shifts.controllers.exceptions;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.net.MediaType.JSON_UTF_8;
import static io.github.kglowins.shifts.enums.Environment.PROD;
import static spark.Spark.exception;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.dto.ErrorMessageDTO;
import io.github.kglowins.shifts.enums.Environment;
import spark.Response;

@Singleton
public class ErrorHandler {

    private Environment env;

    @Inject
    public ErrorHandler(Environment env) {
        this.env = env;
        setUpErrorHandling(this.env);
    }

    public void setUpErrorHandling(Environment env) {
        exception(JsonSyntaxException.class, (exception, request, response) -> {
            handleException(exception, response, env, 400, "Request body is not a valid JSON");
        });
        exception(BadRequestException.class, (exception, request, response) -> {
            handleException(exception, response, env, 400);
        });
        exception(NotFoundException.class, (exception, request, response) -> {
            handleException(exception, response, env, 404);
        });
        exception(UnauthorizedException.class, (exception, request, response) -> {
            handleException(exception, response, env, 403);
        });
        exception(Exception.class, (exception, request, response) -> {
            handleException(exception, response, env, 500);
        });
    }

    private static void handleException(Exception exception, Response response, Environment env, Integer statusCode ) {
        handleException(exception, response, env, statusCode, exception.getMessage());
    }

    private static void handleException(Exception exception, Response response, Environment env, Integer statusCode, String customErrorMessage) {
        String stackTrace = null;
        if (!PROD.equals(env)) {
            stackTrace = getStackTraceAsString(exception);
        }
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customErrorMessage, stackTrace);
        response.type(JSON_UTF_8.toString());
        response.body(new Gson().toJson(errorMessageDTO));
        response.status(statusCode);
    }
}
