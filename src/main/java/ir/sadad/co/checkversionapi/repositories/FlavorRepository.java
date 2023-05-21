package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlavorRepository extends JpaRepository<Flavor, Long> {

    Optional<Flavor> findByApplicationInfoIdAndFlavorName(Long appId, String flavorName);

}
