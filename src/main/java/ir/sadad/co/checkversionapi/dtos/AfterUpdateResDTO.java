package ir.sadad.co.checkversionapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AfterUpdateResDTO {

    @Schema(title = "کد ورژن نهایی", description = "شامل ورژن آخر براساس اپلیکیشن کاریر")
    private Integer lastVersionCode;

    @Schema(title = "نشان دهنده اینکه آیا کاربر به ورژن آخر بروز رسانی کرده است یا خیر")
    private Boolean isLastVersion = false;

    @Schema(title = "وضعیت ورژن پس از بروزرسانی بر اساس اپلیکشن")
    @NotNull
    private Status appStatus;

    @Schema(title = "لیست فیچرهای افزوده شده از ورژن قبلی تا ورژن جاری", nullable = true)
    private List<Feature> features;
}
