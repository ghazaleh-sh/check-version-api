package ir.sadad.co.checkversionapi.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatusReqDto implements Serializable {
    private static final long serialVersionUID = 746888539400045345L;
    private Integer statusCode;
    private String statusTitle;
    private String statusDescription;

}
