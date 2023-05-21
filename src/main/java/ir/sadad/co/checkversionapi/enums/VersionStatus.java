package ir.sadad.co.checkversionapi.enums;

import lombok.Getter;

@Getter
public enum VersionStatus {
    ForceUpdate(1),
    Updated(3),
    UnableToUpdate(4),
    TEST(5);

    private final int value;

    VersionStatus(int value) {
        this.value = value;
    }
}

