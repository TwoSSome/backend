package towssome.server.advice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import towssome.server.dto.ErrorResult;
import towssome.server.exception.*;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundCalendarException(NotFoundCalendarException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundCalendarException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> notFoundCalendarException(NotFoundEntityException e) {
        ErrorResult errorResult = new ErrorResult("NotFoundException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> bodyTooLongException(BodyOverException e) {
        ErrorResult errorResult = new ErrorResult("BodyOverException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    // 데이터 무결성 위반 예외 처리 (예: 문자열 길이 초과, 외래 키 제약 위반 등)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>("데이터 무결성 위반: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Valid 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
