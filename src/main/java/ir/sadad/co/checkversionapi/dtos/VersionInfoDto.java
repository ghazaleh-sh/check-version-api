package ir.sadad.co.checkversionapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import ir.sadad.co.checkversionapi.entities.Status;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VersionInfoDto {
    private String versionName;
    private Integer versionCode;
    private Date validityDate;
    private boolean lastVersion;
    private Status appStatus;
    private ApplicationInfo applicationInfo;
    private List<FeatureObj> features;
    private List<BSObj> businessRules;

    @Data
    public static class FeatureObj {
        private Long id;
        private String featureTitle;
        private String featureType;
        private BigInteger featureCode;
        private String featureDescription;
        private List<SubFeatureObj> subFeatures;
        private boolean featureEnable;
    }

    @Data
    public static class SubFeatureObj{
        private Long id;
        private boolean newSubFeature;
    }

    @Data
    public static class BSObj {
        private Long id;
        private String bsTitle;
        private Integer osCode;
        private Integer osVersion;
    }
}
