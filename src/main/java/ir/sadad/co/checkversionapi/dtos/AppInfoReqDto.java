package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppInfoReqDto implements Serializable {
    private static final long serialVersionUID = -1912314187455493695L;
    private String appName;
    private Integer osCode;
    private String appDescription;
}
