package ir.sadad.co.checkversionapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.sadad.co.checkversionapi.utils.ApiGeneralResponses;
import ir.sadad.co.checkversionapi.commons.validators.CheckSsnCrudUser;
import ir.sadad.co.checkversionapi.dtos.*;
import ir.sadad.co.checkversionapi.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static ir.sadad.co.checkversionapi.commons.Constants.SSN;

/**
 * for sending check version request and getting added features
 * <p>
 * adds, gets and edits data of versions and its details by panel
 *
 * @author g.shahrokhabadi
 * created on 2021/12/28
 */
@RestController
@RequestMapping("${v1API}/version")
@Tag(description = "مستندات سرویس های چک ورژن", name = "Version Resources ")
@RequiredArgsConstructor
@Validated
public class CheckVersionController {

    private final VersionService versionService;

    @GetMapping(value = "/history")
    @ApiGeneralResponses
    @Operation(summary = "سرویس دریافت تاریخچه تغییرات ",
            description = "این سرویس بر اساس ورژن و اطلاعات اپلیکیشن، تغییرات افزوده شده تا آخرین ورژن به همراه پیغام مناسب بروزرسانی را نمایش میدهد.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ChangeHistoryResDTO.class)))
    public ResponseEntity<ChangeHistoryResDTO> changeHistory(@Valid ChangeHistoryReqDTO changeHisReqDTO) {
        ChangeHistoryResDTO resDTO = versionService.changeHistory(changeHisReqDTO);
        return new ResponseEntity<>(resDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/history/after-update")
    @ApiGeneralResponses
    @Operation(summary = "سرویس دریافت تغییرات پس از بروزسانی ",
            description = "این سرویس تغییرات افزوده شده پس از بروزرسانی را نمایش میدهد.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AfterUpdateResDTO.class)))
    public ResponseEntity<AfterUpdateResDTO> afterUpdate(@Valid AfterUpdateReqDTO afterUpdateReqDTO) {
        AfterUpdateResDTO resDTO = versionService.afterUpdate(afterUpdateReqDTO);
        return new ResponseEntity<>(resDTO, HttpStatus.OK);
    }

    //------------------------------- crud services---------------------------//

    @PostMapping(value = "/add-version")
    @ApiGeneralResponses
    @Operation(summary = "سرویس افزودن ورژن جدید ",
            description = "در این سرویس به همراه مشخصات ورژن جدید، ویژگی ها و محدودیتهای مربوط به آن نیز افزوده میشوند")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AddVersionReqDto.class)))
    public ResponseEntity<HttpStatus> addVersion(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                 @RequestBody AddVersionReqDto addVersionReqDto) {

        versionService.addVersion(addVersionReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    @ApiGeneralResponses
    @Operation(summary = "دریافت وضعیتها ",
            description = "لیستی از کل وضعیت های ثبت شده نمایش داده میشود")
    public ResponseEntity<GetStatusesDto> getStatus(@RequestHeader(SSN) @CheckSsnCrudUser String ssn) {

        GetStatusesDto allStatus = versionService.getAllStatus();
        return new ResponseEntity<>(allStatus, HttpStatus.OK);
    }

    @GetMapping("/app")
    @ApiGeneralResponses
    @Operation(summary = "دریافت اطلاعات اپلیکیشن ها ",
            description = "لیستی از کل اپلیکیشنهای ثبت شده نمایش داده میشود")
    public ResponseEntity<GetApplicationsInfoDto> getAppInfo(@RequestHeader(SSN) @CheckSsnCrudUser String ssn) {
        GetApplicationsInfoDto allAppInfo = versionService.getAllAppInfo();
        return new ResponseEntity<>(allAppInfo, HttpStatus.OK);
    }

    @GetMapping("/feature")
    @ApiGeneralResponses
    @Operation(summary = "دریافت ویژگی های فعال ",
            description = "لیستی از اطلاعات ویژگی های فعال را نمایش میدهد")
    public ResponseEntity<GetEnableFeaturesDto> getEnableFeatures(@RequestHeader(SSN) @CheckSsnCrudUser String ssn) {
        GetEnableFeaturesDto allEnableFeature = versionService.getEnableFeatures();
        return new ResponseEntity<>(allEnableFeature, HttpStatus.OK);
    }

    @PostMapping("/add-status")
    @ApiGeneralResponses
    @Operation(summary = "افزودن وضعیت جدید ",
            description = "این سرویس اطلاعات وضعیت جدید را اضافه مینماید.")
    public ResponseEntity<HttpStatus> addStatus(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                @RequestBody StatusReqDto statusReqDto) {
        versionService.addStatus(statusReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-app")
    @ApiGeneralResponses
    @Operation(summary = "افزودن اطلاعات اپلیکیشن جدید ",
            description = "این سرویس اطلاعات اپلیکیشن جدید را اضافه مینماید.")
    public ResponseEntity<HttpStatus> addAppInfo(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                 @RequestBody AppInfoReqDto appInfoReqDto) {
        versionService.addAppInfo(appInfoReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-status/{id}")
    @ApiGeneralResponses
    @Operation(summary = "بروزرسانی اطلاعات وضعیت ",
            description = "این سرویس اطلاعات وضعیت درخواستی بر اساس شناسه را بروزرسانی مینماید.")
    public ResponseEntity<HttpStatus> updateStatus(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                   @PathVariable("id") Long id,
                                                   @RequestBody StatusReqDto statusReqDto) {
        versionService.updateStatus(id, statusReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-app/{id}")
    @ApiGeneralResponses
    @Operation(summary = "بروزرسانی اطلاعات اپلیکیشن ",
            description = "این سرویس اطلاعات اپلیکیشن درخواستی بر اساس شناسه را بروزرسانی مینماید.")
    public ResponseEntity<HttpStatus> updateAppInfo(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                    @PathVariable("id") Long id,
                                                    @RequestBody AppInfoReqDto appInfoReqDto) {
        versionService.updateAppInfo(id, appInfoReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/feature-enable/{id}")
    @ApiGeneralResponses
    @Operation(summary = "فعال/غیرفعال کردن وضعیت ویژگی ",
            description = "این سرویس وضعیت ویژگی درخواستی را بروزرسانی مینماید.")
    public ResponseEntity<HttpStatus> updateFeatureEnable(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                          @PathVariable("id") Long id,
                                                          @RequestBody FeatureEnableReqDto enableDto) {
        versionService.updateFeatureEnable(id, enableDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{versionId}")
    @ApiGeneralResponses
    @Operation(summary = " بروزرسانی تمامی اطلاعات ورژن",
            description = "این سرویس اطلاعات ورژن، فیچرها، ساب فیچرها و محدودیتهای مربوط به آن را به طور همزمان بروز رسانی و در صورت نیاز ساب فیچر جدید نیز ایجاد مینماید. ")
    public ResponseEntity<HttpStatus> updateVersionWithBsAndFeatures(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                                     @PathVariable("versionId") Long versionId,
                                                                     @Valid @RequestBody UpdateVersionReqDto updateDto) {
        versionService.updateVersion(versionId, updateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{versionId}")
    @ApiGeneralResponses
    @Operation(summary = " دریافت اطلاعات ورژن",
            description = "این سرویس اطلاعات ورژن، فیچرها، ساب فیچرها و محدودیتهای مربوط به آن را نمایش میدهد. ")
    public ResponseEntity<VersionInfoDto> getVersion(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                     @PathVariable("versionId") Long versionId) {
        VersionInfoDto versionInfo = versionService.getVersion(versionId);
        return new ResponseEntity<>(versionInfo, HttpStatus.OK);
    }

    @GetMapping
    @ApiGeneralResponses
    @Operation(summary = "دریافت لیست ورژن ها ",
            description = "لیستی از کل اطلاعات مستر ورژن نمایش داده میشود")
    public ResponseEntity<GetVersionsDto> getVersions(@RequestHeader(SSN) @CheckSsnCrudUser String ssn) {
        GetVersionsDto versions = versionService.getAllVersions();
        return new ResponseEntity<>(versions, HttpStatus.OK);
    }

    @GetMapping("/flavors")
    @ApiGeneralResponses
    @Operation(summary = "دریافت لیست فلیورها ",
            description = "لیستی از کل اطلاعات فلیورها نمایش داده میشود")
    public ResponseEntity<GetFlavorsDto> getFlavors(@RequestHeader(SSN) @CheckSsnCrudUser String ssn) {
        GetFlavorsDto flavors = versionService.getAllFlavors();
        return new ResponseEntity<>(flavors, HttpStatus.OK);
    }

    @PostMapping("/add-flavor")
    @ApiGeneralResponses
    @Operation(summary = "افزودن فلیور جدید ",
            description = "این سرویس فلیور را اضافه مینماید.")
    public ResponseEntity<HttpStatus> addFlavor(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                 @RequestBody FlavorReqDto flavorReqDto) {
        versionService.addFlavor(flavorReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-flavor/{id}")
    @ApiGeneralResponses
    @Operation(summary = "بروزرسانی اطلاعات فلیور ",
            description = "این سرویس اطلاعات فلیور درخواستی بر اساس شناسه را بروزرسانی مینماید.")
    public ResponseEntity<HttpStatus> updateFlavor(@RequestHeader(SSN) @CheckSsnCrudUser String ssn,
                                                   @PathVariable("id") Long id,
                                                   @RequestBody FlavorReqDto flavorReqDto) {
        versionService.updateFlavor(id, flavorReqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
