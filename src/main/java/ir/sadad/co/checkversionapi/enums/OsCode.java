package ir.sadad.co.checkversionapi.enums;

public enum OsCode {

    ANDROID(1),
    IOS(2);

    private Integer value;

    OsCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
