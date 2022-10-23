package ir.sadad.co.checkversionapi.commons.exceptions;

import ir.sadad.co.checkversionapi.utils.Empty;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseBusinessException extends RuntimeException {

    protected String code;
    protected HttpStatus httpStatusCode;
    protected String extraData;
    private Map<String, Object> messageArgs;

    public BaseBusinessException() {
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    protected BaseBusinessException(Throwable cause) {
        super(cause);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    public BaseBusinessException(String message) {
        super(message);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    public BaseBusinessException(String message, Throwable cause) {
        super(message, cause);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    public BaseBusinessException(String message, Throwable cause,
                                 boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    public BaseBusinessException(List<BaseBusinessException> exceptions) {
        addMessageExceptions(exceptions);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    public Object getMessageArg(String key) {
        if (Empty.isEmpty(messageArgs)) {
            return "";
        } else {
            return messageArgs.get(key);
        }
    }

    public void addMessageArg(String messageArg, Object messageVal) {
        if (Empty.isEmpty(this.messageArgs)) {
            this.messageArgs = new HashMap<>();
        }
        this.messageArgs.put(messageArg, messageVal);
    }


    public void addMessageExceptions(final List<BaseBusinessException> exceptions) {
        if (Empty.isEmpty(this.messageArgs)) {
            this.messageArgs = new HashMap<>();
        }
        this.messageArgs.put("exceptions", exceptions);
    }

    public Map<String, Object> getMessageArgs() {
        if (Empty.isNotEmpty(messageArgs)) {
            return messageArgs;
        }
        return null;
    }

    public abstract String getErrorCode();

    public HttpStatus getHttpStatus() {
        return this.httpStatusCode;
    }

    public String getExtraData() {
        return this.extraData;
    }

}