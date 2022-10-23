package ir.sadad.co.checkversionapi.dtos.mappers;

import ir.sadad.co.checkversionapi.dtos.GetVersionsDto;
import ir.sadad.co.checkversionapi.dtos.VersionInfoDto;
import ir.sadad.co.checkversionapi.entities.BusinessRule;
import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Version;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VersionMapper {
    List<VersionInfoDto.FeatureObj> toFeatureDto(List<Feature> entity);

    List<VersionInfoDto.BSObj> toBusinessRuleDto(List<BusinessRule> entity);

//    @Mappings({
//            @Mapping(source = "versionEntity.status.id", target = "appStatusId"),
//            @Mapping(source = "versionEntity.applicationInfo.id", target = "applicationInfoId")})
    List<GetVersionsDto> toGetVersionsDto(List<Version> versionEntity);
}
