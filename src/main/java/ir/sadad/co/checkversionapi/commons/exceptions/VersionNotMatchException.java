package ir.sadad.co.checkversionapi.commons.exceptions;

import ir.sadad.co.checkversionapi.commons.exceptions.BaseBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VersionNotMatchException extends BaseBusinessException {

    @Override
    public String getErrorCode() {
        return "version.with.app.id.not.found";
    }
}
