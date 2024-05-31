package towssome.server.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import towssome.server.dto.ErrorResult;
import towssome.server.exception.*;

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

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundMemberException(UsernameNotFoundException e) {
        ErrorResult errorResult = new ErrorResult("UsernameNotFoundException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundMateException(NotFoundMatingException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundMatingException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> pageException(PageException e) {
        ErrorResult errorResult = new ErrorResult("PageException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundReplyException(NotFoundReplyException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundReplyException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notMatchReviewTypeException(NotMatchReviewTypeException e) {
        ErrorResult errorResult = new ErrorResult("NotMatchReviewTypeException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> validEmailExpirationException(ExpirationEmailException e) {
        ErrorResult errorResult = new ErrorResult("ExpirationEmailException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> noSendEmailException(EmailSendException e) {
        ErrorResult errorResult = new ErrorResult("EmailSendException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

}
