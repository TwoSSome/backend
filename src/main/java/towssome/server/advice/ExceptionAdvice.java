package towssome.server.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import towssome.server.dto.ErrorResult;
import towssome.server.exception.DuplicateIdException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> duplicateIdException(DuplicateIdException e) {
        ErrorResult errorResult = new ErrorResult("DuplicateIdException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
