package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.enums.FeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Feature findByIdAndFeatureEnableTrue(Long featureId);

    List<Feature> findAllByFeatureEnableTrue();

    Feature findByIdAndFeatureEnableTrueAndFeatureTypeIsNot(Long featureId, FeatureType type);
}
