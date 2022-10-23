package ir.sadad.co.checkversionapi.services;

import ir.sadad.co.checkversionapi.dtos.*;
import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Status;
import ir.sadad.co.checkversionapi.entities.Version;

import java.util.List;

/**
 * a service for fetching and checking version info
 *
 * @author g.shahrokhabadi
 * created on 2021/12/28
 */
public interface VersionService {

    /**
     * <p>
     *  this method will be called each time an application started.
     *  gets appId, version code of client and os object.
     *  checks if appId compatibles with version code. Also checks os information and throws exception if each has problems.
     *  then defines status of version based on enabled version, validity date of version and business rules of os.
     *  if we want to show features(by featureReturn in {@link ChangeHistoryReqDTO} param), fetches enables features of greater versions.
     * </p>
     *
     * @param changeHistoryReqDTO
     * @return
     */
    ChangeHistoryResDTO changeHistory(ChangeHistoryReqDTO changeHistoryReqDTO);

    /**
     * <p>
     *     this method will be called when client updates her/his application.
     *     checks if appId compatibles with version code.
     *     then defines status of version based on enabled version, validity date of version and business rules of os.
     *     so, the added features between old and new versions will be showed.
     * </p>
     * @param afterUpdateReqDTO
     * @return
     */
    AfterUpdateResDTO afterUpdate(AfterUpdateReqDTO afterUpdateReqDTO);

    void addVersion(AddVersionReqDto addVersionReqDto);

    GetStatusesDto getAllStatus();

    GetApplicationsInfoDto getAllAppInfo();

    GetEnableFeaturesDto getEnableFeatures();

    void addStatus(StatusReqDto statusReqDto);

    void addAppInfo(AppInfoReqDto appInfoReqDto);

    void updateStatus(Long id, StatusReqDto statusReqDto);

    void updateAppInfo(Long id, AppInfoReqDto appInfoReqDto);

    void updateFeatureEnable(Long id, FeatureEnableReqDto enableDto);

    void updateVersion(Long versionId, UpdateVersionReqDto versionUpdateReqDto);

    VersionInfoDto getVersion(Long versionId);

    GetVersionsDto getAllVersions();
}
