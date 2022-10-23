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
public class GetStatusesDto {
    private List<GetStatusesDto.GetStatusDto> getStatusDtoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetStatusDto {

        private Long id;
        private Integer statusCode;
        private String statusTitle;
        private String statusDescription;
    }
}
