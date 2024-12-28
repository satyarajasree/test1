package com.rajasreeit.backend.repo.crmEmployeeRepos;

import com.rajasreeit.backend.entities.CrmEmployee;
import com.rajasreeit.backend.entities.crmEmployeeEntities.BankAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepo extends JpaRepository<BankAccounts, Integer> {

    List<BankAccounts> findByCrmEmployee(CrmEmployee crmEmployee);
}
