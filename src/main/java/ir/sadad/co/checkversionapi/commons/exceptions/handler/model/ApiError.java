package ir.sadad.co.checkversionapi.commons.exceptions.handler.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.*;


@Data
public class ApiError {

    private HttpStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING, timezone = "Asia/Tehran")
    private Date timestamp;

    private String code;

    private String message;

    private String localizedMessage;

    private List<ApiSubError> subErrors;

    private Map<String, Object> meta;

    private String extraData;

    private ApiError() {

        timestamp = new Date();
        this.subErrors = new ArrayList<>();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonAnyGetter
    public Map<String, Object> getMeta() {
        if (meta == null) {
            meta = new HashMap<>();
        }

        return meta;
    }

    private void addSubError(ApiSubError subError) {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        subErrors.add(subError);
    }

    public void addValidationError(String field, String object,
                                   String code, String message, String localizedMessage) {
        addSubError(new ApiValidationError(field, object, code, message, localizedMessage));
    }

    public void addValidationError(String code,
                                   String message, String localizedMessage) {
        addSubError(new ApiValidationError(code, message, localizedMessage));
    }

    /**
     * Utility method for adding error of ConstraintViolation. Usually when a @Validated
     * validation fails.
     *
     * @param cv the ConstraintViolation
     */
    private void addValidationError(ConstraintViolation<?> cv) {
        this.addValidationError(null, cv.getRootBeanClass().getSimpleName(),
                ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
                cv.getInvalidValue() == null ? "" : cv.getInvalidValue().toString(),
                cv.getMessage());
    }

    public void addValidationError(FieldError fieldError) {
        this.addValidationError("", fieldError.getObjectName(), fieldError.getField(),
                fieldError.getRejectedValue() == null ? ""
                        : fieldError.getRejectedValue().toString(),
                fieldError.getDefaultMessage());
    }

    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }

    public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
        constraintViolations.forEach(this::addValidationError);
    }

    interface ApiSubError {

    }

    public static class Builder {
        private HttpStatus status;

        private Date timestamp;

        private String code;

        private String message;

        private String localizedMessage;

        private List<ApiSubError> subErrors;

        private Map<String, Object> meta;

        private String extraData;

        private Builder() {
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder extraData(String extraData) {
            this.extraData = extraData;
            return this;
        }

        public Builder subErrors(List<ApiSubError> subErrors) {
            this.subErrors = subErrors;
            return this;
        }

        public Builder localizedMessage(String localizedMessage) {
            this.localizedMessage = localizedMessage;
            return this;
        }


        public Builder meta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        public ApiError build() {
            ApiError apiError = new ApiError();

            apiError.setStatus(status);
            apiError.setTimestamp(timestamp);
            apiError.setLocalizedMessage(localizedMessage);
            apiError.setCode(code);
            apiError.setMessage(message);
            apiError.setSubErrors(subErrors);
            apiError.setMeta(meta);
            apiError.setExtraData(extraData);

            return apiError;
        }
    }

    class ApiValidationError implements ApiSubError {

        private String object;

        private String field;

        private String code;

        private String message;

        private String localizedMessage;

        public ApiValidationError() {
        }

        public ApiValidationError(String code,
                                  String message,
                                  String localizedMessage) {
            this.code = code;
            this.message = message;
            this.localizedMessage = localizedMessage;
        }

        public ApiValidationError(String field, String object,
                                  String code, String message, String localizedMessage) {
            this.field = field;
            this.object = object;
            this.code = code;
            this.message = message;
            this.localizedMessage = localizedMessage;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLocalizedMessage() {
            return localizedMessage;
        }

        public void setLocalizedMessage(String localizedMessage) {
            this.localizedMessage = localizedMessage;
        }
    }
}