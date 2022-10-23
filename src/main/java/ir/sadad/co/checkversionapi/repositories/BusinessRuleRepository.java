package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.BusinessRule;
import ir.sadad.co.checkversionapi.entities.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, Long> {

    Optional<BusinessRule> findByVersionAndOsCodeAndOsVersion(Version version, Integer osCode, Integer osVersion);

    Optional<BusinessRule> findByIdAndVersion(Long id, Version version);

    List<BusinessRule> findByVersion(Version version);
}

