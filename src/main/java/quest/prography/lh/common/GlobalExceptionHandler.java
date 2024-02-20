package quest.prography.lh.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import quest.prography.lh.common.exception.GameRuleViolationException;
import quest.prography.lh.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GameRuleViolationException.class)
    public ResponseEntity<ApiResponse> handleSodaException(GameRuleViolationException e) {
        LOGGER.debug(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse> handleException(Throwable e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error());
    }
}
