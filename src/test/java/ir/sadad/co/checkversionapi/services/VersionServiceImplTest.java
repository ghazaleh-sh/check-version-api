package ir.sadad.co.checkversionapi.services;

import ir.sadad.co.checkversionapi.dtos.AddVersionReqDto;
import ir.sadad.co.checkversionapi.dtos.AfterUpdateReqDTO;
import ir.sadad.co.checkversionapi.dtos.ChangeHistoryReqDTO;
import ir.sadad.co.checkversionapi.dtos.UpdateVersionReqDto;
import ir.sadad.co.checkversionapi.repositories.VersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ActiveProfiles(profiles = {"qa"})
class VersionServiceImplTest {

    @Autowired
    private VersionService versionService;

    @Autowired
    private VersionRepository versionRepository;

    @Test
    void addVersion() {
        List<AddVersionReqDto.FeatureObj> featureList = new ArrayList<>();

        AddVersionReqDto.FeatureObj featureObj1 = AddVersionReqDto.FeatureObj.builder()
                .featureCode(new BigInteger("2"))
                .featureTitle("خرید نون غ")
                .featureType("BUGFIX")
                .build();
        AddVersionReqDto.FeatureObj featureObj2 = AddVersionReqDto.FeatureObj.builder()
                .featureCode(new BigInteger("1"))
                .featureTitle("گرانولا غ")
                .featureType("FEATURE")
                .build();

        featureList.add(featureObj1);
        featureList.add(featureObj2);

        AddVersionReqDto addVersionReqDto = AddVersionReqDto.builder()
                .applicationId(1L)
                .statusId(3L)
                .versionCode(40101581)
                .versionName("4.1.1581")
                .features(featureList)
                .build();

        versionService.addVersion(addVersionReqDto);

        assertTrue(versionRepository.findByVersionCodeAndApplicationInfoId(40101581, 1L).get().isEnabled());

    }

    @Test
    void updateVersion() {

        long versionId = 3L;

        UpdateVersionReqDto updateVersionReqDto = UpdateVersionReqDto.builder()
//                .applicationId(1L)
                .statusId(3L)
                .validityDate(null)
                .build();

        versionService.updateVersion(versionId, updateVersionReqDto);

        assertNull(versionRepository.findById(versionId).get().getValidityDate());

    }

    /**
     * for checking version status
     */
    @Test
    void changeHistory() {
        ChangeHistoryReqDTO reqDTO = new ChangeHistoryReqDTO();
        reqDTO.setReturnFeature(true);
        reqDTO.setOsVersion(9);
        reqDTO.setOsCode("IOS");
        reqDTO.setAppId("DEMO");
        reqDTO.setClientVersionCode(40101564);

        assertEquals(2, versionService.changeHistory(reqDTO).getAppStatus().getStatusCode());
    }

    @Test
    void afterUpdate() {
        AfterUpdateReqDTO reqDTO = new AfterUpdateReqDTO();
        reqDTO.setAppId("DEMO");
        reqDTO.setOldVersionCode(40101564);
        reqDTO.setCurrentVersionCode(40101581);

        assertEquals(2, versionService.afterUpdate(reqDTO).getFeatures().size());
    }
}