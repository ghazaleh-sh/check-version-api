package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeatureEnableReqDto implements Serializable {
    private static final long serialVersionUID = 2533603736755170979L;
    private boolean featureEnable;
}
