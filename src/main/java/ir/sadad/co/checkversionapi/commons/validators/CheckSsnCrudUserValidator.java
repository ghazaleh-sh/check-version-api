package ir.sadad.co.checkversionapi.commons.validators;


import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class CheckSsnCrudUserValidator implements ConstraintValidator<CheckSsnCrudUser, String> {

    @Value(value = "${checkversion.crud-users-info.ssn}")
    private String allSsn;

    @Override
    public boolean isValid(String nationalCode, ConstraintValidatorContext Context) {

        if (nationalCode == null) {
            return false;
        }

        if (nationalCode.length() != 10) {
            return false;
        }
        if (!nationalCode.matches("^\\d{10}$")) {
            return false;
        }
        if(!checkUserInfo(nationalCode))
            return false;

        int sum = 0;
        int lenght = 10;
        for (int i = 0; i < lenght - 1; i++) {
            sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
        }

        int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

        int c = sum % 11;

        return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
    }

    private boolean checkUserInfo(String nationalCode) {
        List<String> ssnList = Arrays.asList(allSsn.split("-"));
        return ssnList.contains(nationalCode);
    }
}
