package ir.sadad.co.checkversionapi.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationName {

    PRODUCTION(1),
    PWA(2),
    DEVELOP(3),
    DEMO(4);

    private final Integer value;

    public int getValue() {
        return value;
    }
}
