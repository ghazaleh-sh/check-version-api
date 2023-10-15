package ir.sadad.co.checkversionapi.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * In order to bind request parameters to object you need to have standard getters/setters (@Data) in your DTO class.
 * then you can bind in controller without even any annotation (such as @RequestParam).
 */
@Data
public class ChangeHistoryReqDTO implements Serializable {

    private static final long serialVersionUID = 3790648927575247299L;
    @Schema(title = "شناسه اپلیکیشن")
    @NotNull(message = "app.id.is.required")
    @Pattern(regexp = "^(PRODUCTION|PWA|DEVELOP|DEMO)$", message = "{app.id.not.valid}")
    private String appId;

    @Schema(title = "ورژن کد کاربر")
    @NotNull(message = "version.code.is.required")
    private Integer clientVersionCode;

    @Schema(title = "کد سیستم عامل")
    @NotNull(message = "os.code.is.required")
    @Pattern(regexp = "^(ANDROID|IOS|WEB)$", message = "{os.code.not.valid}")
    private String osCode;

    @Schema(title = "ورژن سیستم عامل")
    @NotNull(message = "os.version.is.required")
    private Integer osVersion;

    @Schema(title = "درخواست ارسال فیچرها", nullable = true, defaultValue = "false")
    private Boolean returnFeature = false;

    @Schema(title = "عنوان فلیور")
    private String flavorName;

}
