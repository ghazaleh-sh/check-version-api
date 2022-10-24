package ir.sadad.co.checkversionapi.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * In order to bind request parameters to object you need to have standard getters/setters (@Data) in your DTO class.
 * then you can bind in controller without even any annotation (such as @RequestParam).
 */
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
