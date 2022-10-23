package ir.sadad.co.checkversionapi.repositories;

import ir.sadad.co.checkversionapi.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Status findByStatusCode(Integer code);
}
