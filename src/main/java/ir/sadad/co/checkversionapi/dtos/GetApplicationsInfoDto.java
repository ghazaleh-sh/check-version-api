package ir.sadad.co.checkversionapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetApplicationsInfoDto {
    private List<GetApplicationsInfoDto.GetAppInfoDto> getAppInfoDtoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAppInfoDto {

        private Long id;
        private String appName;
        private String appDescription;
        private Integer osCode;
    }
}
