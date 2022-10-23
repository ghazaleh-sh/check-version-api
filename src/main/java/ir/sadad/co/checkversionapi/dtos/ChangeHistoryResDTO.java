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
public class ChangeHistoryResDTO {

    /**
     * last version code of app
     */
    @Schema(title = "کد ورژن نهایی", description = "شامل ورژن آخر براساس اپلیکیشن کاریر")
    @NotNull
    private Integer lastVersionCode;

    @Schema(title = "عنوان", nullable = true)
    private String lastVersionName;

    @Schema(title = "وضعیت ورژن جاری کاربر")
    @NotNull
    private Status appStatus;

    @Schema(title = "لیست فیچرهای اضافه شده از ورژن جاری کاربر تا ورژن نهایی", nullable = true)
    private List<Feature> features;

}
