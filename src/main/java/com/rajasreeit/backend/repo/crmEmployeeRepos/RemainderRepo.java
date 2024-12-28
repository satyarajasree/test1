package com.rajasreeit.backend.repo.crmEmployeeRepos;

import com.rajasreeit.backend.entities.crmEmployeeEntities.RemainderDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemainderRepo extends JpaRepository<RemainderDates, Integer> {

    List<RemainderDates> findByCrmEmployeeId(int crmEmployeeId);
}
