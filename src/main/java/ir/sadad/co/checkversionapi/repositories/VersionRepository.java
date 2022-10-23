package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

    Optional<Version> findByVersionCodeAndApplicationInfoId(Integer versionCode, Long appId);

    Version findTopByApplicationInfoIdOrderByVersionCodeDesc(Long Id);

    List<Version> findByApplicationInfoIdAndVersionCodeGreaterThanOrderByVersionCodeDesc(Long Id, Integer versionCode);

    List<Version> findByApplicationInfoIdAndVersionCodeBetween(Long Id, Integer OldVersionCode, Integer currentVersionCode);

    Optional<Version> findByApplicationInfoIdAndEnabledIsTrue(Long Id);
}
