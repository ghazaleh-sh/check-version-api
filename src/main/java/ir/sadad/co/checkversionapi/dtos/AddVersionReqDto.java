package ir.sadad.co.checkversionapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AddVersionReqDto implements Serializable {
    private static final long serialVersionUID = -9126296016777934542L;
    @NotNull(message = "version.name.is.required")
    private String versionName;

    @NotNull(message = "version.code.is.required")
    private Integer versionCode;

    @NotNull(message = "app.id.is.required")
    private Long applicationId;

    @NotNull
    private Long statusId;

    private Date validityDate;

    @NotNull
    private List<FeatureObj> features;

    private List<BSObj> businessRules;


    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureObj {
        @NotNull
        private String featureTitle;

        @NotNull
        private String featureType;

        private BigInteger featureCode;

        private String featureDescription;

        private List<SubFeatureObj> subFeatures;


    }

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

    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BSObj {
        @NotNull
        private String bsTitle;

        @NotNull
        private Integer osCode;

        @NotNull
        private Integer osVersion;


    }
}
