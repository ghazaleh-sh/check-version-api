package ir.sadad.co.checkversionapi.dtos;

import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Status;
import ir.sadad.co.checkversionapi.enums.FeatureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEnableFeaturesDto {
    private List<GetEnableFeaturesDto.GetEnableFeatureDto> getFeatureDtoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetEnableFeatureDto {
        private Long id;
        private String featureTitle;
        private FeatureType featureType;
        private boolean featureEnable;
        private BigInteger featureCode;
        private String featureDescription;
        private List<Feature> subFeatures;
        private Long appId;
    }
}
