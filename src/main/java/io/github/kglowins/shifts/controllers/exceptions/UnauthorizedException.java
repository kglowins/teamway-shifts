package io.github.kglowins.shifts.controllers.exceptions;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(String message) {
        super(message);
    }
}
