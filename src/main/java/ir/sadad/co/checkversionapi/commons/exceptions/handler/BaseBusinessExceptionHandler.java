package ir.sadad.co.checkversionapi.commons.exceptions.handler;


import ir.sadad.co.checkversionapi.commons.exceptions.BaseBusinessException;
import ir.sadad.co.checkversionapi.commons.exceptions.handler.model.ApiError;
import ir.sadad.co.checkversionapi.utils.Empty;
import ir.sadad.co.checkversionapi.utils.MessageFiller;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import java.util.*;

import static org.springframework.http.HttpStatus.*;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class BaseBusinessExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_GENERAL_VALIDATION = "EBPR.general.validation";
    private static final String ERROR_GENERAL_DB_CONSTRAINT_VIOLATION = "EBPR.general.db.constraint.violation";
    private static final String ERROR_GENERAL_DB_CONNECTION_EXCEPTION = "EBPR.general.db.connection.exception";
    private static final String ERROR_GENERAL_DB_EXCEPTION = "EBPR.general.db.execute.exception";
    private static final String ERROR_METHOD_ARGUMENT_NOT_VALID = "EBPR.general.validator.EBP40000002";
    private static final String ERROR_CONSTRAINT_VIOLATION = "EBPR.general.validator.EBP40000001";
    private static final String ERROR_INTERNAL_SERVER = "EBPR.general.internal.server.exception";
    private static final Locale LOCALE_EN = Locale.ENGLISH;
    private static final Locale LOCALE_FA = new Locale("fa");

    private final MessageSource messageSource;
    private final MessageFiller messageFiller;

    public BaseBusinessExceptionHandler(MessageSource messageSource, MessageFiller messageFiller) {
        this.messageSource = messageSource;
        this.messageFiller = messageFiller;
    }


    @ExceptionHandler(BaseBusinessException.class)
    protected ResponseEntity<Object> handleBaseBusinessException(
            BaseBusinessException ex, final HttpServletRequest request) {
        ApiError apiError = new ApiError(ex.getHttpStatus());

        apiError.setMessage(initializeMessage(ex.getErrorCode(), ex.getMessageArgs(), LOCALE_EN));
        apiError.setLocalizedMessage(initializeMessage(ex.getErrorCode(), ex.getMessageArgs(), LOCALE_FA));
        List<BaseBusinessException> subExceptions;
        List<String> messages = new ArrayList<>();
        if (ex.getMessageArgs() != null && !ex.getMessageArgs().isEmpty()) {
            subExceptions = getSubExceptions(ex.getMessageArgs());
            for (BaseBusinessException subException : subExceptions) {
                String message = initializeMessage(subException.getErrorCode(), subException.getMessageArgs(), LOCALE_FA);
                messages.add(message);
            }
        }
        apiError.setCode(ex.getErrorCode());
        //apiError.setSubMessages(messages);
        apiError.setSubErrors(new ArrayList<>());
        apiError.setExtraData(ex.getExtraData());
        if (Empty.isNotEmpty(ex.getMessageArgs())) {
            Map<String, Object> meta = new HashMap<>();
            meta.putAll(ex.getMessageArgs());
            apiError.setMeta(meta);
        }

        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setCode(ERROR_METHOD_ARGUMENT_NOT_VALID);
        apiError.setMessage(
                initializeMessage(ERROR_METHOD_ARGUMENT_NOT_VALID,
                        null,
                        LOCALE_EN));
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_METHOD_ARGUMENT_NOT_VALID,
                        null,
                        LOCALE_FA));
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            apiError.addValidationError(fieldError.getField(), fieldError.getObjectName(),
                    fieldError.getDefaultMessage(),
                    initializeMessage(fieldError.getDefaultMessage(), null, LOCALE_EN),
                    initializeMessage(fieldError.getDefaultMessage(), null, LOCALE_FA));
        }

        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(value = {javax.validation.ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);

        apiError.setCode(ERROR_CONSTRAINT_VIOLATION);
        apiError.setMessage(
                initializeMessage(ERROR_CONSTRAINT_VIOLATION,
                        null,
                        LOCALE_EN));
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_CONSTRAINT_VIOLATION,
                        null,
                        LOCALE_FA));

        for (ConstraintViolation constraintViolation : ex.getConstraintViolations()) {
            apiError.addValidationError(constraintViolation.getMessage(),
                    initializeMessage(constraintViolation.getMessage(), null, LOCALE_EN),
                    initializeMessage(constraintViolation.getMessage(), null, LOCALE_FA));
        }

        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(ApiError.builder().status(BAD_REQUEST)
                .code("error.general.bad-request")
                .message(error)
                .localizedMessage(initializeMessage("error.general.bad-request", null, LOCALE_FA)).build(), ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        String error = builder.substring(0, builder.length() - 2);

        return buildResponseEntity(ApiError.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .code("error.general.unsupported-media-types").message(error).build(), ex);
    }

    @ExceptionHandler(value = JDBCConnectionException.class)
    protected ResponseEntity<Object> handleHibernateJDBCConnectionException(
            JDBCConnectionException ex) {

        ApiError apiError = new ApiError(UNAVAILABLE_FOR_LEGAL_REASONS);
        apiError.setCode(ERROR_GENERAL_DB_CONNECTION_EXCEPTION);
        apiError.setMessage("Connection error: " + ex.getMessage());
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_GENERAL_DB_CONNECTION_EXCEPTION,
                        null,
                        LOCALE_FA));

        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleHibernateConstraintViolation(
            ConstraintViolationException ex) {

        ApiError apiError = new ApiError(UNAVAILABLE_FOR_LEGAL_REASONS);
        apiError.setCode(ERROR_GENERAL_DB_CONSTRAINT_VIOLATION);
        apiError.setMessage("Validation error: " + ex.getConstraintName());
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_GENERAL_DB_CONSTRAINT_VIOLATION,
                        null,
                        LOCALE_FA));

        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(value = HibernateException.class)
    protected ResponseEntity<Object> handleHibernateExceptions(HibernateException ex) {

        ApiError apiError = new ApiError(UNAVAILABLE_FOR_LEGAL_REASONS);
        apiError.setCode(ERROR_GENERAL_DB_EXCEPTION);
        apiError.setMessage("DB error: " + ex.getMessage());
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_GENERAL_DB_EXCEPTION,
                        null,
                        LOCALE_FA));

        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(value = Throwable.class)
    protected ResponseEntity<Object> handleInternalServerExceptions(Throwable ex, final HttpServletRequest request) {

        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
        apiError.setCode(ERROR_INTERNAL_SERVER);
        apiError.setMessage(ex.getMessage());
        apiError.setLocalizedMessage(
                initializeMessage(ERROR_INTERNAL_SERVER,
                        null,
                        LOCALE_FA));

        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(ApiError.builder().status(BAD_REQUEST)
                .code("error.general.malformed-json-request").message(error).build(), ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        String error = "Error writing JSON output";

        return buildResponseEntity(
                ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .code("error.general.json-write-output").message(error).build(), ex);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setCode("error.general.no-handler");
        apiError.setMessage(String.format("Could not find the %s method for URL %s",
                ex.getHttpMethod(), ex.getRequestURL()));

        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setCode("error.general.method-argument-type-mispatch");
        apiError.setMessage(String.format(
                "The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));

        return buildResponseEntity(apiError, ex);
    }

    protected ResponseEntity<Object> buildResponseEntity(ApiError apiError, Throwable ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }


    protected String initializeMessage(String errorCode, Map<String, Object> messageParameters, Locale locale) {
        String message = messageSource.getMessage(errorCode, null, locale);
        message = messageFiller.fill(message, messageParameters);

        return message;
    }

    private List<BaseBusinessException> getSubExceptions(Map<String, Object> messageParameters) {
        if (messageParameters.containsKey("exceptions")) {
            return (List<BaseBusinessException>) messageParameters.get("exceptions");
        }
        return Collections.emptyList();
    }

    private String exceptionMessage(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        Throwable t = e.getCause();
        while (t != null) {
            sb.append("\nCaused by: ").append(t.getMessage());
            t = t.getCause();
        }
        sb.append("#############################################");
        sb.append(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
        return sb.toString();
    }
}