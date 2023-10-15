package ir.sadad.co.checkversionapi.services;

import ir.sadad.co.checkversionapi.dtos.*;
import ir.sadad.co.checkversionapi.dtos.mappers.UpdateMapper;
import ir.sadad.co.checkversionapi.dtos.mappers.VersionMapper;
import ir.sadad.co.checkversionapi.entities.*;
import ir.sadad.co.checkversionapi.enums.ApplicationName;
import ir.sadad.co.checkversionapi.enums.FeatureType;
import ir.sadad.co.checkversionapi.enums.OsCode;
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
    private final FlavorRepository flavorRepository;

    @SneakyThrows
    @Override
    public ChangeHistoryResDTO changeHistory(ChangeHistoryReqDTO changeHistoryReqDTO) {
        Long appId = (long) ApplicationName.valueOf(changeHistoryReqDTO.getAppId()).getValue();
        Version savedVersion = checkVersionCodeWithAppId(changeHistoryReqDTO.getClientVersionCode(), appId);

        Status savedStatus = checkVersionCodeStatus(savedVersion, OsCode.valueOf(changeHistoryReqDTO.getOsCode()).getValue(), changeHistoryReqDTO.getOsVersion());

        log.info("version and status fetched correctly...");
        Set<Feature> features = new HashSet<>();
        List<Version> versionsGtCurrentCode = versionRepository.findByApplicationInfoIdAndVersionCodeGreaterThanOrderByVersionCodeDesc(appId, savedVersion.getVersionCode());

        Version lastVersion = versionsGtCurrentCode.isEmpty() ? savedVersion : versionsGtCurrentCode.stream().findFirst().get();

        if (changeHistoryReqDTO.getReturnFeature()) {
            if (savedStatus.getStatusCode().equals(VersionStatus.UnableToUpdate.getValue())) {
                versionFeatureRepository.findByVersion(savedVersion).forEach(versionFeature ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(versionFeature.getFeature().getId(), FeatureType.SUB_FEATURE)));

            } else if (!versionsGtCurrentCode.isEmpty()) {
                versionsGtCurrentCode.forEach(v -> versionFeatureRepository.findByVersion(v).forEach(a ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(a.getFeature().getId(), FeatureType.SUB_FEATURE))));

            } else {
                versionFeatureRepository.findByVersion(lastVersion).forEach(versionFeature ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(versionFeature.getFeature().getId(), FeatureType.SUB_FEATURE)));
            }

        }

        log.info("last version fetched correctly....");
        List<Feature> featureListWithSub = setEnableSubFeatures(filterGeneralBugFix(features.stream()
                .filter(Objects::nonNull).collect(Collectors.toList())));

        Optional<String> downloadLink = getDownloadLink(appId, changeHistoryReqDTO.getFlavorName());

        ChangeHistoryResDTO changeHistoryResDTO = new ChangeHistoryResDTO();
        changeHistoryResDTO.setLastVersionCode(lastVersion.getVersionCode());
        changeHistoryResDTO.setFeatures(featureListWithSub);
        changeHistoryResDTO.setLastVersionName(lastVersion.getVersionName());
        changeHistoryResDTO.setAppStatus(savedStatus);
        changeHistoryResDTO.setCurrentDate(System.currentTimeMillis()); // equivalent Instant.now().toEpochMilli()  -  modern format
        changeHistoryResDTO.setDownloadLink(downloadLink.orElse(null));
        changeHistoryResDTO.setSilent(savedVersion.isSilent());
        if (savedVersion.getValidityDate() != null)
            changeHistoryResDTO.setValidityDate(savedVersion.getValidityDate().getTime());

        return changeHistoryResDTO;
    }

    private List<Feature> filterGeneralBugFix(List<Feature> features) {

        Optional<Feature> firstGeneralBugFix = features.stream()
                .filter(feature -> feature.getFeatureType().equals(FeatureType.GENERAL_BUGFIX))
                // to compare features based on their IDs and returns an Optional containing the feature with the highest ID.
                .max(Comparator.comparingLong(Feature::getId));

        if (firstGeneralBugFix.isPresent()) {
            List<Feature> filteredFeatures = features.stream()
                    .filter(feature -> !feature.getFeatureType().equals(FeatureType.GENERAL_BUGFIX))
                    .collect(Collectors.toList());

            filteredFeatures.add(firstGeneralBugFix.get());
            features.clear();
            features.addAll(filteredFeatures);
        }
        return features;

    }

    private Optional<String> getDownloadLink(Long appId, String flavorName) {

        return flavorRepository.findByApplicationInfoIdAndFlavorName(appId, flavorName)
                .map(Flavor::getDownloadLink);

    }

    @SneakyThrows
    @Override
    public AfterUpdateResDTO afterUpdate(AfterUpdateReqDTO afterUpdateReqDTO) {
        Long appId = (long) ApplicationName.valueOf(afterUpdateReqDTO.getAppId()).getValue();
        Version savedOldVersion = checkVersionCodeWithAppId(afterUpdateReqDTO.getOldVersionCode(), appId);
        Version savedCurrentVersion = checkVersionCodeWithAppId(afterUpdateReqDTO.getCurrentVersionCode(), appId);

//        Status savedCurrentStatus = checkVersionCodeStatus(savedCurrentVersion, null, null);

        log.info("both old and current versions have been fetched correctly...");
        Set<Feature> features = new HashSet<>();
        List<Version> versionsBetweenCodes = versionRepository.findByApplicationInfoIdAndVersionCodeBetween(appId, savedOldVersion.getVersionCode(), savedCurrentVersion.getVersionCode());
        if (!versionsBetweenCodes.isEmpty()) {
            if (!savedCurrentVersion.getVersionCode().equals(savedOldVersion.getVersionCode()))
                versionsBetweenCodes.stream().filter(version -> version != savedOldVersion).forEach(v -> versionFeatureRepository.findByVersion(v).forEach(a ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(a.getFeature().getId(), FeatureType.SUB_FEATURE))
                ));
            else
                versionFeatureRepository.findByVersion(savedCurrentVersion).forEach(a ->
                        features.add(featureRepository.findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(a.getFeature().getId(), FeatureType.SUB_FEATURE))
                );
        }
        log.info("features between 2 versions listed...");
        List<Feature> featureListWithSub = setEnableSubFeatures(filterGeneralBugFix(features.stream().filter(Objects::nonNull).collect(Collectors.toList())));

        AfterUpdateResDTO afterUpdateResDTO = new AfterUpdateResDTO();
        afterUpdateResDTO.setLastVersionCode(versionRepository.findTopByApplicationInfoIdOrderByVersionCodeDesc(appId).getVersionCode());
        afterUpdateResDTO.setFeatures(featureListWithSub);
//        afterUpdateResDTO.setAppStatus(savedCurrentStatus);
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
        newVersion.setSilent(addVersionReqDto.getSilent());
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

        //versionCode & versionName should not be updated
        mapper.updateVersionFromDto(versionUpdateReqDto, existedVersion);
        versionRepository.saveAndFlush(existedVersion);

        if (versionUpdateReqDto.getFeatures() != null) {
            versionUpdateReqDto.getFeatures().forEach(f -> {
                if (f.getId() != null) {
                    Feature existedFeature = checkEntityById(featureRepository, f.getId(), "feature");
                    versionFeatureRepository.findByVersionAndFeature(existedVersion, existedFeature).orElseThrow(() -> new NotFoundException("version.feature"));
                    mapper.updateFeatureFromDto(f, existedFeature);
                    featureRepository.saveAndFlush(existedFeature);

                    if (f.getSubFeatures() != null) {
                        f.getSubFeatures().forEach(sf -> {
                            Optional<SubFeature> sub = subFeatureRepository.findByIdAndFeature(sf.getId(), existedFeature);
                            if (sub.isEmpty()) {
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
                if (bs.getId() != null) {
                    Optional<BusinessRule> bsReq = businessRuleRepository.findByIdAndVersion(bs.getId(), existedVersion);
                    BusinessRule existedBS = bsReq.orElseThrow(() -> new NotFoundException("businessRule"));
                    mapper.updateBSFromDto(bs, existedBS);
                    businessRuleRepository.saveAndFlush(existedBS);
                } else {
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
        versionInfoDto.setSilent(savedVersion.isSilent());
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
        List<GetEnableFeaturesDto.GetEnableFeatureDto> featuresList = new ArrayList<>();

        featureRepository.findAllByFeatureEnableTrue()
                .forEach(feature -> {
                    GetEnableFeaturesDto.GetEnableFeatureDto eachFeature = new GetEnableFeaturesDto.GetEnableFeatureDto();
                    modelMapper.map(feature, eachFeature);
                    versionFeatureRepository.findByFeature(feature)
                            .forEach(versionFeature -> {
                                versionRepository.findById(versionFeature.getVersion().getId())
                                        .ifPresent(version -> eachFeature.setAppId(version.getApplicationInfo().getId()));
                            });
                    featuresList.add(eachFeature);

                });

        enableFeatures.setGetFeatureDtoList(featuresList);

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

    @Override
    public GetFlavorsDto getAllFlavors() {
        GetFlavorsDto allFlavor = new GetFlavorsDto();
        allFlavor.setFlavorsList(flavorRepository.findAll().stream()
                .map(flavor -> modelMapper.map(flavor, GetFlavorsDto.GetFlavorDto.class)).collect(Collectors.toList()));

        return allFlavor;
    }

    @Override
    public void addFlavor(FlavorReqDto flavorReqDto) {
        if (flavorReqDto.getFlavorName() == null || "".equals(flavorReqDto.getFlavorName()) ||
                flavorReqDto.getDownloadLink() == null || "".equals(flavorReqDto.getDownloadLink()) || flavorReqDto.getApplicationId() == null)
            throw new NotFoundException("flavor.data");

        ApplicationInfo existedApp = checkEntityById(applicationInfoRepository, flavorReqDto.getApplicationId(), "application");

        Flavor newFlavor = new Flavor();
        newFlavor.setApplicationInfo(existedApp);
        newFlavor.setFlavorName(flavorReqDto.getFlavorName());
        newFlavor.setDownloadLink(flavorReqDto.getDownloadLink());
        flavorRepository.saveAndFlush(newFlavor);
    }

    @Override
    public void updateFlavor(Long id, FlavorReqDto flavorReqDto) {
        Flavor existedFlavor = checkEntityById(flavorRepository, id, "flavor");

        if (flavorReqDto.getApplicationId() != null
                && (!flavorReqDto.getApplicationId().equals(existedFlavor.getApplicationInfo().getId()))) {

            existedFlavor.setApplicationInfo(checkEntityById(applicationInfoRepository,
                    flavorReqDto.getApplicationId(), "application"));
        }
        mapper.updateFlavorFromDto(flavorReqDto, existedFlavor);

        flavorRepository.saveAndFlush(existedFlavor);
    }


    private Version checkVersionCodeWithAppId(Integer versionCode, Long appId) {
        Optional<Version> version = versionRepository.findByVersionCodeAndApplicationInfoId(versionCode, appId);
        return version.orElseThrow(VersionNotMatchException::new);

    }

    private boolean checkVersionBusinessRule(Version savedVersion, Integer osCode, Integer ocVersion) {

        Optional<BusinessRule> businessRule = businessRuleRepository.findByVersionAndOsCodeAndOsVersion(savedVersion, osCode, ocVersion);
        return businessRule.isPresent();

    }

    private Status checkVersionCodeStatus(Version savedVersion, Integer osCode, Integer osVersion) {
        int statusCode;
        if (savedVersion.isEnabled())
            statusCode = 3; //updated
        else {
            /*
            for checking a full day before expiration date use:
            TimeUnit.DAYS.convert((savedVersion.getValidityDate().getTime() - System.currentTimeMillis()), TimeUnit.MILLISECONDS) <= 0
             */
            if (osCode != null && osVersion != null && checkVersionBusinessRule(savedVersion, osCode, osVersion))
                statusCode = 4; //unable to update

            else if (savedVersion.getValidityDate() != null &&
                    (savedVersion.getValidityDate().getTime() - System.currentTimeMillis()) <= 0)
                statusCode = 1;

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
