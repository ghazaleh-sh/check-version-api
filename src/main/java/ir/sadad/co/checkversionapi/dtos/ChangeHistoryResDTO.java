package ir.sadad.co.checkversionapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChangeHistoryResDTO implements Serializable {

    private static final long serialVersionUID = -3524187060646412357L;
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

    @Schema(title = "تاریخ انقضا ورژن جاری کاربر")
    private Long validityDate;

    /**
     * To obtain the current instant using the system clock, you can use the Instant.now() method.
     * To convert this instant to the number of milliseconds from the epoch, use the toEpochMilli() method.
     * <p>
     * you should use System.currentTimeMillis() because it's a native method that delegates the task to the underlying operating system
     * (this calculation usually is very optimized and doesn't require garbage collection etc).
     */
    @Schema(title = "تاریخ جاری سیستم")
    private Long currentDate;

    @Schema(title = "لیست فیچرهای اضافه شده از ورژن جاری کاربر تا ورژن نهایی", nullable = true)
    private List<Feature> features;

    @Schema(title = "لینک دانلود فلیور ارسالی")
    private String downloadLink;

}
