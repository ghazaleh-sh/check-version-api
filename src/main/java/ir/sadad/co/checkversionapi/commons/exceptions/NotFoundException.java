package ir.sadad.co.checkversionapi.commons.exceptions;

import ir.sadad.co.checkversionapi.commons.exceptions.BaseBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseBusinessException {

    private static final long serialVersionUID = 5728795467068093005L;

    public NotFoundException(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public String getErrorCode() {
        return extraData+".not.found";
    }
}