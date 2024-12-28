package com.rajasreeit.backend.service.crmEmployeeServices;

import com.rajasreeit.backend.dto.CrmEmployeeProfileDto;
import com.rajasreeit.backend.entities.CrmEmployee;
import com.rajasreeit.backend.entities.crmEmployeeEntities.BankAccounts;
import com.rajasreeit.backend.repo.CrmEmployeeRepo;
import com.rajasreeit.backend.repo.crmEmployeeRepos.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private BankRepo bankRepo;

    @Autowired
    private CrmEmployeeRepo crmEmployeeRepo;


    public CrmEmployee updateCrmProfile(int id, CrmEmployeeProfileDto crmEmployeeProfileDto) {
        // Fetch employee
        CrmEmployee crmEmployee = crmEmployeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update image paths
        if (crmEmployeeProfileDto.getProfileImagePath() != null && !crmEmployeeProfileDto.getProfileImagePath().isEmpty()) {
            crmEmployee.setProfileImagePath(crmEmployeeProfileDto.getProfileImagePath());
        }
        if (crmEmployeeProfileDto.getIdCardPath() != null && !crmEmployeeProfileDto.getIdCardPath().isEmpty()) {
            crmEmployee.setIdCardPath(crmEmployeeProfileDto.getIdCardPath());
        }

        // Save and return updated employee
        return crmEmployeeRepo.save(crmEmployee);
    }

    public CrmEmployee getEmployee(int id){
        return crmEmployeeRepo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }


    // Add a new bank account
    public BankAccounts addBankAccount(BankAccounts bankAccount) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<CrmEmployee> employeeOpt = Optional.ofNullable(crmEmployeeRepo.findByMobile(username));

        if (employeeOpt.isPresent()) {
            CrmEmployee employee = employeeOpt.get();
            bankAccount.setCrmEmployee(employee);
            return bankRepo.save(bankAccount);
        } else {
            throw new RuntimeException("Employee not found");
        }
    }

    // Get all bank accounts associated with an employee using employee id
    public List<BankAccounts> getBankAccountsByEmployeeId(int employeeId) {
        // Fetch employee
        CrmEmployee crmEmployee = crmEmployeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        // Fetch and return the list of bank accounts associated with the employee
        return bankRepo.findByCrmEmployee(crmEmployee);
    }


    public BankAccounts getBankAccountById(int id) {
        return bankRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
    }

}
