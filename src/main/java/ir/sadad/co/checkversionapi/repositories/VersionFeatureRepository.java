package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.Version;
import ir.sadad.co.checkversionapi.entities.VersionFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VersionFeatureRepository extends JpaRepository<VersionFeature, Long> {

    List<VersionFeature> findByVersion(Version version);

    Optional<VersionFeature> findByVersionAndFeature(Version version, Feature feature);

    List<VersionFeature> findByFeature(Feature feature);

}
