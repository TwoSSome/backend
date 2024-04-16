package towssome.server.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import towssome.server.dto.ErrorResult;
import towssome.server.exception.DuplicateIdException;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.exception.NotFoundReviewPostException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> duplicateIdException(DuplicateIdException e) {
        ErrorResult errorResult = new ErrorResult("DuplicateIdException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundReviewPostException(NotFoundReviewPostException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundReviewPostException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundMemberException(NotFoundMemberException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundMemberException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

}