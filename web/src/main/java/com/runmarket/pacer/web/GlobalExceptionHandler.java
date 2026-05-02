package com.runmarket.pacer.web;

import com.runmarket.pacer.domain.exception.EmailAlreadyExistsException;
import com.runmarket.pacer.domain.exception.InvalidCredentialsException;
import com.runmarket.pacer.domain.exception.InvalidVerificationTokenException;
import com.runmarket.pacer.domain.exception.TurnstileVerificationException;
import com.runmarket.pacer.domain.exception.UserNotVerifiedException;
import com.runmarket.pacer.web.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException e) {
        log.warn("Authentication failed: user={}", SecurityUtils.currentUserEmail());
        return problem(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        log.warn("Email already exists: user={}", SecurityUtils.currentUserEmail());
        return problem(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidToken(InvalidVerificationTokenException e) {
        log.warn("Invalid verification token: user={}", SecurityUtils.currentUserEmail());
        return problem(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ProblemDetail> handleUserNotVerified(UserNotVerifiedException e) {
        log.warn("User not verified: user={}", SecurityUtils.currentUserEmail());
        return problem(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(TurnstileVerificationException.class)
    public ResponseEntity<ProblemDetail> handleTurnstile(TurnstileVerificationException e) {
        log.warn("Turnstile verification failed: user={}", SecurityUtils.currentUserEmail());
        return problem(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NoSuchElementException e) {
        log.warn("Resource not found: user={}, message={}", SecurityUtils.currentUserEmail(), e.getMessage());
        return problem(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception e) {
        log.error("Unexpected error: user={}", SecurityUtils.currentUserEmail(), e);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.");
        problem.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}
