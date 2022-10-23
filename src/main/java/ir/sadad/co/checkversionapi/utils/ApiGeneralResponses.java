package ir.sadad.co.checkversionapi.utils;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ir.sadad.co.checkversionapi.commons.exceptions.handler.model.ApiError;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "مقادیر ورودی سرویس نامعتبر هستند", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "احراز هویت غیر مجاز می باشد", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "شما به سرویس موردنظر دسترسی ندارید", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "نتیجه ای یافت نشد", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "424", description = "خطا در فراخوانی سرویس های وابسته", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "خطای نا مشخص در انجام عملیات", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
})
public @interface ApiGeneralResponses {
}