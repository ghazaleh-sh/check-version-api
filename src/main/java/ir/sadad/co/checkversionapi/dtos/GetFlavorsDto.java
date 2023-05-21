package ir.sadad.co.checkversionapi.dtos;

import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFlavorsDto {
    private List<GetFlavorsDto.GetFlavorDto> flavorsList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetFlavorDto {
        private Long id;
        private String flavorName;
        private String downloadLink;
        private ApplicationInfo applicationInfo;
    }
}
