package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

@Data
public class StatusReqDto {
    private Integer statusCode;
    private String statusTitle;
    private String statusDescription;

}
