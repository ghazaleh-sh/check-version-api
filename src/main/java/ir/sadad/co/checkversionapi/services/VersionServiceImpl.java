package ir.sadad.co.checkversionapi.services;

import ir.sadad.co.checkversionapi.dtos.*;
import ir.sadad.co.checkversionapi.dtos.mappers.UpdateMapper;
import ir.sadad.co.checkversionapi.dtos.mappers.VersionMapper;
import ir.sadad.co.checkversionapi.entities.*;
import ir.sadad.co.checkversionapi.enums.FeatureType;
import ir.sadad.co.checkversionapi.enums.VersionStatus;
import ir.sadad.co.checkversionapi.commons.exceptions.MoreThanOneNewVersionException;
import ir.sadad.co.checkversionapi.commons.exceptions.NotFoundException;
import ir.sadad.co.checkversionapi.commons.exceptions.VersionNotMatchException;
import ir.sadad.co.checkversionapi.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * a service for checking changes based on version code
 *
 * @author g.shahrokhabadi
 * created on 2021/12/20
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VersionServiceImpl implements VersionService {

    private final VersionRepository versionRepository;
    private final BusinessRuleRepository businessRuleRepository;
    private final VersionFeatureRepository versionFeatureRepository;
    private final StatusRepository statusRepository;
    private final FeatureRepository featureRepository;
    private final SubFeatureRepository subFeatureRepository;
    private final ApplicationInfoRepository applicationInfoRepository;
    private final UpdateMapper mapper;
    private final VersionMapper versionMapper;
    private final ModelMapper modelMapper;

    @SneakyThrows
    @Override
    public ChangeHistoryResDTO changeHistory(ChangeHistoryReqDTO changeHistoryReqDTO) {

        Version savedVersion = checkVersionCodeWithAppId(changeHistoryReqDTO.getClientVersionCode(), changeHistoryReqDTO.getAppId());

        Status savedStatus = checkVersionCodeStatus(savedVersion, changeHistoryReqDTO.getOs());

        log.info("version and status fetched correctly...");
        Set<Feature> features = new HashSet<>();
        List<Version> versionsGtCurrentCode = versionRepository.findByApplicationInfoIdAndVersionCodeGreaterThanOrderByVersionCodeDesc(changeHistoryReqDTO.getAppId(), savedVersion.getVersionCode());
        if (changeHistoryReqDTO.getReturnFeature() && !versionsGtCurrentCode.isEmpty()) {
            versionsGtCurrentCode.forEach(v -> versionFeatureRepository.findByVersion(v).forEach(a ->
                    features.add(featureRepository.findByIdAndFeatureEnableTrue(a.getFeature().getId()))
            ));
        }

        Version lastVersion;
        if (versionsGtCurrentCode.isEmpty()) {
            lastVersion = savedVersion;
            if (changeHistoryReqDTO.getReturnFeature())
                versionFeatureRepository.findByVersion(lastVersion).forEach(a ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrue(a.getFeature().getId())));
        } else {
            lastVersion = versionsGtCurrentCode.stream().findFirst().get();
        }
        log.info("last version fetched correctly....");
        List<Feature> featureListWithSub = setEnableSubFeatures(features.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        ChangeHistoryResDTO changeHistoryResDTO = new ChangeHistoryResDTO();
        changeHistoryResDTO.setLastVersionCode(lastVersion.getVersionCode());
        changeHistoryResDTO.setFeatures(featureListWithSub);
        changeHistoryResDTO.setLastVersionName(lastVersion.getVersionName());
        changeHistoryResDTO.setAppStatus(savedStatus);

        return changeHistoryResDTO;
    }

    @SneakyThrows
    @Override
    public AfterUpdateResDTO afterUpdate(AfterUpdateReqDTO afterUpdateReqDTO) {
        Version savedOldVersion = checkVersionCodeWithAppId(afterUpdateReqDTO.getOldVersionCode(), afterUpdateReqDTO.getAppId());
        Version savedCurrentVersion = checkVersionCodeWithAppId(afterUpdateReqDTO.getCurrentVersionCode(), afterUpdateReqDTO.getAppId());

        Status savedCurrentStatus = checkVersionCodeStatus(savedCurrentVersion, null);

        log.info("both old and current versions and status fetched correctly...");
        Set<Feature> features = new HashSet<>();
        List<Version> versionsBetweenCodes = versionRepository.findByApplicationInfoIdAndVersionCodeBetween(afterUpdateReqDTO.getAppId(), savedOldVersion.getVersionCode(), savedCurrentVersion.getVersionCode());
        if (!versionsBetweenCodes.isEmpty()) {
            versionsBetweenCodes.stream().filter(version -> version != savedOldVersion).forEach(v -> versionFeatureRepository.findByVersion(v).forEach(a ->
                    features.add(featureRepository.findByIdAndFeatureEnableTrue(a.getFeature().getId()))
            ));
        }
        log.info("features between 2 versions listed...");
        List<Feature> featureListWithSub = setEnableSubFeatures(features.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        AfterUpdateResDTO afterUpdateResDTO = new AfterUpdateResDTO();
        afterUpdateResDTO.setLastVersionCode(versionRepository.findTopByApplicationInfoIdOrderByVersionCodeDesc(afterUpdateReqDTO.getAppId()).getVersionCode());
        afterUpdateResDTO.setFeatures(featureListWithSub);
        afterUpdateResDTO.setAppStatus(savedCurrentStatus);
        if (afterUpdateResDTO.getLastVersionCode().equals(afterUpdateReqDTO.getCurrentVersionCode()))
            afterUpdateResDTO.setIsLastVersion(true);
        return afterUpdateResDTO;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addVersion(AddVersionReqDto addVersionReqDto) {
        Status existedStatus = checkEntityById(statusRepository, addVersionReqDto.getStatusId(), "status");

        ApplicationInfo existedApp = checkEntityById(applicationInfoRepository, addVersionReqDto.getApplicationId(), "application");

        Version newVersion = new Version();
        try {
            List<Version> previousGreaterVersions = versionRepository.findByApplicationInfoIdAndVersionCodeGreaterThanOrderByVersionCodeDesc(
                    existedApp.getId(), addVersionReqDto.getVersionCode());

            if (previousGreaterVersions.isEmpty()) {
                newVersion.setEnabled(true);
                versionRepository.findByApplicationInfoIdAndEnabledIsTrue(existedApp.getId()).ifPresent(v -> {
                    v.setEnabled(false);
                    versionRepository.saveAndFlush(v);
                });
            }

        } catch (Exception e) {
            throw new MoreThanOneNewVersionException();
        }

        newVersion.setVersionName(addVersionReqDto.getVersionName());
        newVersion.setVersionCode(addVersionReqDto.getVersionCode());
        newVersion.setValidityDate(addVersionReqDto.getValidityDate());
        newVersion.setApplicationInfo(existedApp);
        newVersion.setStatus(existedStatus);
        versionRepository.saveAndFlush(newVersion);

        addVersionReqDto.getFeatures().forEach(f -> createNewFeature(f, newVersion));

        log.info("version, feature, versionFeature and subFeature tables inserted successfully....");

        if (addVersionReqDto.getBusinessRules() != null) {
            addVersionReqDto.getBusinessRules().forEach(bs -> {
                BusinessRule newBS = new BusinessRule();
                newBS.setBsTitle(bs.getBsTitle());
                newBS.setOsCode(bs.getOsCode());
                newBS.setOsVersion(bs.getOsVersion());
                newBS.setVersion(newVersion);
                businessRuleRepository.saveAndFlush(newBS);
            });
        }

    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void updateVersion(Long versionId, UpdateVersionReqDto versionUpdateReqDto) {
        Version existedVersion = checkEntityById(versionRepository, versionId, "version");
        if (versionUpdateReqDto.getStatusId() != null && (!versionUpdateReqDto.getStatusId().equals(existedVersion.getStatus().getId()))) {
            Status existedStatus = checkEntityById(statusRepository, versionUpdateReqDto.getStatusId(), "status");
            existedVersion.setStatus(existedStatus);
        }
        if (versionUpdateReqDto.getApplicationId() != null && (!versionUpdateReqDto.getApplicationId().equals(existedVersion.getApplicationInfo().getId()))) {
            ApplicationInfo existedApp = checkEntityById(applicationInfoRepository, versionUpdateReqDto.getApplicationId(), "application");
            if (existedVersion.isEnabled()) {
                if (versionRepository.findByApplicationInfoIdAndEnabledIsTrue(existedApp.getId()).isPresent())
                    throw new MoreThanOneNewVersionException();
            }
            existedVersion.setApplicationInfo(existedApp);
        }

        mapper.updateVersionFromDto(versionUpdateReqDto, existedVersion);
        versionRepository.saveAndFlush(existedVersion);

        if (versionUpdateReqDto.getFeatures() != null) {
            versionUpdateReqDto.getFeatures().forEach(f -> {
                if(f.getId() != null) {
                    Feature existedFeature = checkEntityById(featureRepository, f.getId(), "feature");
                    versionFeatureRepository.findByVersionAndFeature(existedVersion, existedFeature).orElseThrow(() -> new NotFoundException("version.feature"));
                    mapper.updateFeatureFromDto(f, existedFeature);
                    featureRepository.saveAndFlush(existedFeature);

                    if (f.getSubFeatures() != null) {
                        f.getSubFeatures().forEach(sf -> {
                            Optional<SubFeature> sub = subFeatureRepository.findByIdAndFeature(sf.getId(), existedFeature);
                            if (sub.isPresent()) {
                                mapper.updateSubFeatureFromDto(sf, sub.get());
                                subFeatureRepository.saveAndFlush(sub.get());
                            } else {
                                SubFeature subFeature = new SubFeature();
                                subFeature.setId(sf.getId());
                                subFeature.setFeature(existedFeature);
                                subFeature.setNewSubFeature(sf.isNewSubFeature());
                                subFeatureRepository.saveAndFlush(subFeature);
                            }
                        });
                    }
                } else {
                    createNewFeature(f, existedVersion);
                }
            });
        }

        if (versionUpdateReqDto.getBusinessRules() != null) {
            versionUpdateReqDto.getBusinessRules().forEach(bs -> {
                if(bs.getId() != null) {
                    Optional<BusinessRule> bsReq = businessRuleRepository.findByIdAndVersion(bs.getId(), existedVersion);
                    BusinessRule existedBS = bsReq.orElseThrow(() -> new NotFoundException("businessRule"));
                    mapper.updateBSFromDto(bs, existedBS);
                    businessRuleRepository.saveAndFlush(existedBS);
                }
                else{
                    BusinessRule newBS = new BusinessRule();
                    newBS.setBsTitle(bs.getBsTitle());
                    newBS.setOsCode(bs.getOsCode());
                    newBS.setOsVersion(bs.getOsVersion());
                    newBS.setVersion(existedVersion);
                    businessRuleRepository.saveAndFlush(newBS);
                }
            });
        }

    }

    @Override
    public VersionInfoDto getVersion(Long versionId) {
        Version savedVersion = checkEntityById(versionRepository, versionId, "version");

        Set<Feature> features = new HashSet<>();
        versionFeatureRepository.findByVersion(savedVersion).forEach(f ->
                features.add(featureRepository.findById(f.getFeature().getId()).get())
        );

        List<Feature> featureListWithSub = setSubFeatures(features.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        List<BusinessRule> businessRule = businessRuleRepository.findByVersion(savedVersion);

        VersionInfoDto versionInfoDto = new VersionInfoDto();
        versionInfoDto.setVersionName(savedVersion.getVersionName());
        versionInfoDto.setVersionCode(savedVersion.getVersionCode());
        versionInfoDto.setValidityDate(savedVersion.getValidityDate());
        versionInfoDto.setLastVersion(savedVersion.isEnabled());
        versionInfoDto.setAppStatus(savedVersion.getStatus());
        versionInfoDto.setApplicationInfo(savedVersion.getApplicationInfo());
        versionInfoDto.setFeatures(versionMapper.toFeatureDto(featureListWithSub));
        versionInfoDto.setBusinessRules(versionMapper.toBusinessRuleDto(businessRule));

        return versionInfoDto;
    }

    @Override
    public GetVersionsDto getAllVersions() {
        GetVersionsDto allVersions = new GetVersionsDto();
        allVersions.setGetVersionDtoList(versionRepository.findAll().stream()
                .map(v -> modelMapper.map(v, GetVersionsDto.GetVersionDto.class)).collect(Collectors.toList()));

        return allVersions;
    }

    @Override
    public GetStatusesDto getAllStatus() {
        GetStatusesDto allSts = new GetStatusesDto();
        allSts.setGetStatusDtoList(statusRepository.findAll().stream()
                .map(s -> modelMapper.map(s, GetStatusesDto.GetStatusDto.class)).collect(Collectors.toList()));

        return allSts;
    }

    @Override
    public GetApplicationsInfoDto getAllAppInfo() {
        GetApplicationsInfoDto allApp = new GetApplicationsInfoDto();
        allApp.setGetAppInfoDtoList(applicationInfoRepository.findAll().stream()
                .map(app -> modelMapper.map(app, GetApplicationsInfoDto.GetAppInfoDto.class)).collect(Collectors.toList()));

        return allApp;
    }

    @Override
    public GetEnableFeaturesDto getEnableFeatures() {
        GetEnableFeaturesDto enableFeatures = new GetEnableFeaturesDto();
        enableFeatures.setGetFeatureDtoList(featureRepository.findAllByFeatureEnableTrue().stream()
                .map(app -> modelMapper.map(app, GetEnableFeaturesDto.GetEnableFeatureDto.class)).collect(Collectors.toList()));

        return enableFeatures;
    }

    @Override
    public void addStatus(StatusReqDto statusReqDto) {
        if (statusReqDto.getStatusTitle() == null || "".equals(statusReqDto.getStatusTitle()) || statusReqDto.getStatusCode() == null)
            throw new NotFoundException("is required");
        Status newStatus = new Status();
        newStatus.setStatusCode(statusReqDto.getStatusCode());
        newStatus.setStatusTitle(statusReqDto.getStatusTitle());
        newStatus.setStatusDescription(statusReqDto.getStatusDescription());
        statusRepository.saveAndFlush(newStatus);
    }

    @Override
    public void addAppInfo(AppInfoReqDto appInfoReqDto) {
        if (appInfoReqDto.getAppName() == null || "".equals(appInfoReqDto.getAppName()) || appInfoReqDto.getOsCode() == null)
            throw new NotFoundException("is required");
        ApplicationInfo newApp = new ApplicationInfo();
        newApp.setAppName(appInfoReqDto.getAppName());
        newApp.setOsCode(appInfoReqDto.getOsCode());
        newApp.setAppDescription(appInfoReqDto.getAppDescription());
        applicationInfoRepository.saveAndFlush(newApp);
    }

    @Override
    public void updateStatus(Long id, StatusReqDto statusReqDto) {
        Status existedStatus = checkEntityById(statusRepository, id, "status");
        mapper.updateStatusFromDto(statusReqDto, existedStatus);
        statusRepository.saveAndFlush(existedStatus);

    }

    @Override
    public void updateAppInfo(Long id, AppInfoReqDto appInfoReqDto) {
        ApplicationInfo existedApp = checkEntityById(applicationInfoRepository, id, "application");
        mapper.updateAppInfoFromDto(appInfoReqDto, existedApp);
        applicationInfoRepository.saveAndFlush(existedApp);
    }

    @Override
    public void updateFeatureEnable(Long id, FeatureEnableReqDto enableDto) {
        Feature existedFeature = checkEntityById(featureRepository, id, "feature");
        existedFeature.setFeatureEnable(enableDto.isFeatureEnable());
        featureRepository.saveAndFlush(existedFeature);
    }

    private Version checkVersionCodeWithAppId(Integer versionCode, Long appId) {
        Optional<Version> version = versionRepository.findByVersionCodeAndApplicationInfoId(versionCode, appId);
        return version.orElseThrow(VersionNotMatchException::new);

    }

    private Integer checkVersionBusinessRule(Version savedVersion, ChangeHistoryReqDTO.OsObject savedOs) {

        Optional<BusinessRule> businessRule = businessRuleRepository.findByVersionAndOsCodeAndOsVersion(savedVersion, savedOs.getOsCode(), savedOs.getOsVersion());
        if (businessRule.isPresent())
            return 4;//unable to update

        return 2; //optional update
    }

    private Status checkVersionCodeStatus(Version savedVersion, ChangeHistoryReqDTO.OsObject savedOs) {
        Integer statusCode = 1; //force update
        if (savedVersion.isEnabled())
            statusCode = 3; //updated

        else if (savedVersion.getStatus().getStatusCode().equals(VersionStatus.ForceUpdate.getValue()))
            statusCode = 1;

        else {
            long diffInMilis = Math.abs(new Date().getTime() - savedVersion.getValidityDate().getTime());
            if (TimeUnit.DAYS.convert(diffInMilis, TimeUnit.MILLISECONDS) > 30)
                statusCode = 1;

            else if (savedOs != null) statusCode = checkVersionBusinessRule(savedVersion, savedOs);

            else return statusRepository.findByStatusCode(savedVersion.getStatus().getStatusCode());

        }
        return statusRepository.findByStatusCode(statusCode);

    }

    private List<Feature> setEnableSubFeatures(List<Feature> features) {
        if (!features.isEmpty()) {
            features.forEach(f -> {
                List<SubFeature> subFeatures;
                List<Feature> featuresOfSub = new ArrayList<>();
                subFeatures = subFeatureRepository.findDistinctByFeatureAndNewSubFeatureTrue(f);
                subFeatures.forEach(sub -> featuresOfSub.add(featureRepository.findByIdAndFeatureEnableTrue(sub.getId())));
                f.setSubFeatures(featuresOfSub);
            });
        }
        return features;
    }

    private List<Feature> setSubFeatures(List<Feature> features) {
        if (!features.isEmpty()) {
            features.forEach(f -> {
                List<Feature> featuresOfSub = new ArrayList<>();
                subFeatureRepository.findDistinctByFeature(f).forEach(
                        sub -> featuresOfSub.add(featureRepository.findById(sub.getId()).get()));
                f.setSubFeatures(featuresOfSub);
            });
        }
        return features;
    }

    private static <T> T checkEntityById(JpaRepository<T, Long> rep, Long id, String error) {
        Optional<T> req = rep.findById(id);
        return req.orElseThrow(() -> new NotFoundException(error));

    }

    private <T> void createNewFeature(T f, Version v) {
        Feature newFeature = new Feature();
        if (f instanceof UpdateVersionReqDto.FeatureUpdateObj) {
            newFeature.setFeatureTitle(((UpdateVersionReqDto.FeatureUpdateObj) f).getFeatureTitle());
            newFeature.setFeatureCode(((UpdateVersionReqDto.FeatureUpdateObj) f).getFeatureCode());
            newFeature.setFeatureDescription(((UpdateVersionReqDto.FeatureUpdateObj) f).getFeatureDescription());
            newFeature.setFeatureType(FeatureType.valueOf(((UpdateVersionReqDto.FeatureUpdateObj) f).getFeatureType()));
            newFeature.setFeatureEnable(true);
            featureRepository.saveAndFlush(newFeature);

            VersionFeature versionFeature = new VersionFeature();
            versionFeature.setFeature(newFeature);
            versionFeature.setVersion(v);
            versionFeatureRepository.saveAndFlush(versionFeature);

            if (((UpdateVersionReqDto.FeatureUpdateObj) f).getSubFeatures() != null) {
                ((UpdateVersionReqDto.FeatureUpdateObj) f).getSubFeatures().forEach(sf -> {
                    SubFeature subFeature = new SubFeature();
                    subFeature.setId(sf.getId());
                    subFeature.setFeature(newFeature);
                    subFeature.setNewSubFeature(true);
                    subFeatureRepository.saveAndFlush(subFeature);
                });
            }
        }
        if (f instanceof AddVersionReqDto.FeatureObj) {
            newFeature.setFeatureTitle(((AddVersionReqDto.FeatureObj) f).getFeatureTitle());
            newFeature.setFeatureCode(((AddVersionReqDto.FeatureObj) f).getFeatureCode());
            newFeature.setFeatureDescription(((AddVersionReqDto.FeatureObj) f).getFeatureDescription());
            newFeature.setFeatureType(FeatureType.valueOf(((AddVersionReqDto.FeatureObj) f).getFeatureType()));
            newFeature.setFeatureEnable(true);
            featureRepository.saveAndFlush(newFeature);

            VersionFeature versionFeature = new VersionFeature();
            versionFeature.setFeature(newFeature);
            versionFeature.setVersion(v);
            versionFeatureRepository.saveAndFlush(versionFeature);

            if (((AddVersionReqDto.FeatureObj) f).getSubFeatures() != null) {
                ((AddVersionReqDto.FeatureObj) f).getSubFeatures().forEach(sf -> {
                    SubFeature subFeature = new SubFeature();
                    subFeature.setId(sf.getId());
                    subFeature.setFeature(newFeature);
                    subFeature.setNewSubFeature(true);
                    subFeatureRepository.saveAndFlush(subFeature);
                });
            }
        }
    }
}
