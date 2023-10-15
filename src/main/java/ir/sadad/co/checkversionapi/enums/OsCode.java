package ir.sadad.co.checkversionapi.enums;

public enum OsCode {

    ANDROID(1),
    IOS(2),
    WEB(3);

    private final Integer value;

    OsCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
