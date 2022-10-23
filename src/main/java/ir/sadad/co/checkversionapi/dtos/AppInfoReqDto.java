package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

@Data
public class AppInfoReqDto {
    private String appName;
    private Integer osCode;
    private String appDescription;
}
