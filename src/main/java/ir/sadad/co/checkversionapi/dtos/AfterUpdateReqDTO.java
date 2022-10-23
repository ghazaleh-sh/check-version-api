package ir.sadad.co.checkversionapi.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AfterUpdateReqDTO {
    @Schema(title = " شناسه اپلیکیشن")
    @NotNull(message = "app.id.is.required")
    private Long appId;

    @Schema(title = "کد ورژن قبلی")
    @NotNull(message = "version.code.is.required")
    private Integer oldVersionCode;

    @Schema(title = "کد ورژن جاری")
    @NotNull(message = "version.code.is.required")
    private Integer currentVersionCode;

}
