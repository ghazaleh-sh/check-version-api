package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

@Data
public class FlavorReqDto {

    private String flavorName;
    private String downloadLink;
    private Long applicationId;
}
