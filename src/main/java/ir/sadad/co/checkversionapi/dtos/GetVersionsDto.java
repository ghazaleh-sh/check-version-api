package ir.sadad.co.checkversionapi.dtos;

import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import ir.sadad.co.checkversionapi.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetVersionsDto {
    private List<GetVersionDto> getVersionDtoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetVersionDto {
        private Long id;
        private String versionName;
        private Integer versionCode;
        private Date validityDate;
        private boolean enabled;
        private Status status;
        private ApplicationInfo applicationInfo;
    }
}
