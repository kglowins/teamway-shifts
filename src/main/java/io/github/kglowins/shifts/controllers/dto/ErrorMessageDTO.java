package io.github.kglowins.shifts.controllers.dto;

import com.google.gson.annotations.SerializedName;

public record ErrorMessageDTO(@SerializedName("error_message") String errorMessage,
                              @SerializedName("stack_trace") String stackTrace) {
}
