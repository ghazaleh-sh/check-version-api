package ir.sadad.co.checkversionapi.enums;

import lombok.Getter;

@Getter
public enum VersionStatus {
    ForceUpdate(1),
    Updated(2),
    TEST(3);

    private int value;

    VersionStatus(int value) {
        this.value = value;
    }
}

