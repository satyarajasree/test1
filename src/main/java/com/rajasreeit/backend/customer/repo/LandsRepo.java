package com.rajasreeit.backend.customer.repo;

import com.rajasreeit.backend.customer.entities.Lands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LandsRepo extends JpaRepository<Lands, Integer> {
    @Query("SELECT MAX(l.passbookNumber) FROM Lands l")
    Long getMaxPassbookNumber();
}
