package org.example.ticket.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record APIResponse<T> (Boolean isSuccess,
                              String message,
                              HttpStatus status,
                              T payload,
                              LocalDateTime dateTime) {
}
