package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.ApplicationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationInfoRepository extends JpaRepository<ApplicationInfo, Long> {

//    Optional<ApplicationInfo> findByVersionAndOsCodeAndOsVersion(Version version, Integer osCode, Integer osVersion);
}
