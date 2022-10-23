package ir.sadad.co.checkversionapi.commons.exceptions;

import ir.sadad.co.checkversionapi.commons.exceptions.BaseBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MoreThanOneNewVersionException extends BaseBusinessException {

    private static final long serialVersionUID = 4526346011981620529L;

    @Override
    public String getErrorCode() {
        return "more.than.one.version.per.app.is.new";
    }
}