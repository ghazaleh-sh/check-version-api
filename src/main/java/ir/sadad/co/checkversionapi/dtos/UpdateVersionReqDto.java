package ir.sadad.co.checkversionapi.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVersionReqDto implements Serializable {
    private static final long serialVersionUID = 2079704929565684640L;
    private String versionName;
    private Integer versionCode;
    private Long applicationId;
    private Long statusId;
    private Date validityDate;
    private List<FeatureUpdateObj> features;
    private List<BSUpdateObj> businessRules;
    private Boolean silent;

    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureUpdateObj {
        private Long id;
        private String featureTitle;
        private String featureType;
        private BigInteger featureCode;
        private String featureDescription;
        private List<SubFeatureObj> subFeatures;

        @Data
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubFeatureObj{
            @NotNull(message = "sub.feature.id.is.required")
            private Long id;
            private boolean newSubFeature;
        }
    }

    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BSUpdateObj {
        private Long id;
        private String bsTitle;
        private Integer osCode;
        private Integer osVersion;
    }
}
