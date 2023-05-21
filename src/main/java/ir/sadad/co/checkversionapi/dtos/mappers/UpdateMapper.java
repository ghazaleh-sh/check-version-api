package ir.sadad.co.checkversionapi.dtos.mappers;

import ir.sadad.co.checkversionapi.dtos.AppInfoReqDto;
import ir.sadad.co.checkversionapi.dtos.FlavorReqDto;
import ir.sadad.co.checkversionapi.dtos.StatusReqDto;
import ir.sadad.co.checkversionapi.dtos.UpdateVersionReqDto;
import ir.sadad.co.checkversionapi.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateMapper {

    void updateStatusFromDto(StatusReqDto dto, @MappingTarget Status entity);

    void updateAppInfoFromDto(AppInfoReqDto appInfoReqDto, @MappingTarget ApplicationInfo appInfo);

    @Mapping(target = "validityDate", source = "validityDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateVersionFromDto(UpdateVersionReqDto versionReqDto, @MappingTarget Version existedVersion);

    @Mappings({
            @Mapping(target = "id", source = "id", ignore = true),
            @Mapping(target = "subFeatures", source = "subFeatures", ignore = true)})
    void updateFeatureFromDto(UpdateVersionReqDto.FeatureUpdateObj featureObjDto, @MappingTarget Feature feature);

    @Mapping(target = "id", source = "id", ignore = true)
    void updateBSFromDto(UpdateVersionReqDto.BSUpdateObj bs, @MappingTarget BusinessRule bsEntity);

    void updateSubFeatureFromDto(UpdateVersionReqDto.FeatureUpdateObj.SubFeatureObj sf, @MappingTarget SubFeature existedSub);

    void updateFlavorFromDto(FlavorReqDto flavorReqDto, @MappingTarget Flavor existedFlavor);
}
