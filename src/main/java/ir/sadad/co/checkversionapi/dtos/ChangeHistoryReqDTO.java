package ir.sadad.co.checkversionapi.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ChangeHistoryReqDTO {

    @Schema(title = "شناسه اپلیکیشن")
    @NotNull(message = "app.id.is.required")
    private Long appId;

    @Schema(title = "ورژن کد کاربر")
    @NotNull(message = "version.code.is.required")
    private Integer clientVersionCode;

    @Schema(title = "آبجکت سیستم عامل")
    @NotNull(message = "os.is.required")
    @Valid
    private OsObject os;

    @Schema(title = "درخواست ارسال فیچرها", nullable = true, defaultValue = "false")
    private Boolean returnFeature = false;

    @Data
    @RequiredArgsConstructor
    public static class OsObject {

        @NotNull(message = "os.code.is.required")
        private Integer osCode;

        @NotNull(message = "os.version.is.required")
        private Integer osVersion;

        public OsObject(Integer osCode, Integer osVersion) {
            this.osCode = osCode;
            this.osVersion = osVersion;
        }
    }
}
