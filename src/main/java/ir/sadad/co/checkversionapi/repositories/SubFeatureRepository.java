package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Feature;
import ir.sadad.co.checkversionapi.entities.SubFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubFeatureRepository extends JpaRepository<SubFeature, Long> {

    List<SubFeature> findDistinctByFeatureAndNewSubFeatureTrue(Feature Feature);

    Optional<SubFeature> findByIdAndFeature(Long id, Feature feature);

    List<SubFeature> findDistinctByFeature(Feature Feature);
}