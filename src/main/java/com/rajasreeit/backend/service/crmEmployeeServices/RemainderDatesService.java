package com.rajasreeit.backend.service.crmEmployeeServices;

import com.rajasreeit.backend.dto.RemainderGetDto;
import com.rajasreeit.backend.entities.crmEmployeeEntities.RemainderDates;
import com.rajasreeit.backend.entities.CrmEmployee;
import com.rajasreeit.backend.repo.CrmEmployeeRepo;
import com.rajasreeit.backend.repo.crmEmployeeRepos.RemainderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RemainderDatesService {

    @Autowired
    private RemainderRepo remainderDatesRepo;

    @Autowired
    private CrmEmployeeRepo crmEmployeeRepo;

    // Service method to create a new reminder for the authenticated user
    public RemainderDates createRemainderDateForAuthenticatedUser(RemainderDates remainderDates) {
        // Get the authenticated user's details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // The authenticated user's username (e.g., mobile number)

        // Find the employee by the username (assuming username is the mobile number)
        CrmEmployee crmEmployee = crmEmployeeRepo.findByMobile(username);
        if (crmEmployee == null) {
            throw new RuntimeException("Employee not found for the authenticated user");
        }

        // Set the employee to the remainder date and save it
        remainderDates.setCrmEmployee(crmEmployee);
        return remainderDatesRepo.save(remainderDates);
    }


    public List<RemainderDates> getAllRemaindersByAuthenticatedUser() {
        // Get the authenticated user's details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // The authenticated user's username (e.g., mobile number)

        // Find the employee by the username (assumes username is the mobile number)
        CrmEmployee crmEmployee = crmEmployeeRepo.findByMobile(username);
        if (crmEmployee == null) {
            throw new RuntimeException("Employee not found for the authenticated user");
        }

        // Fetch reminder dates associated with the employee
        return remainderDatesRepo.findByCrmEmployeeId(crmEmployee.getId());
    }


    // Service method to get all remainder dates by the authenticated user's mobile number
    public List<RemainderGetDto> getAllRemainderDatesByAuthenticatedUser() {
        // Get the authenticated user's details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // The authenticated user's mobile number

        // Find the employee by the username (assuming username is the mobile number)
        CrmEmployee crmEmployee = crmEmployeeRepo.findByMobile(username);
        if (crmEmployee == null) {
            throw new RuntimeException("Employee not found for the authenticated user");
        }

        // Fetch remainder dates associated with the employee
        List<RemainderDates> remainders = remainderDatesRepo.findByCrmEmployeeId(crmEmployee.getId());

        // Convert to DTO and return
        return remainders.stream()
                .map(remainder -> {
                    RemainderGetDto dto = new RemainderGetDto();
                    dto.setDate(remainder.getDate());
                    dto.setMessage(remainder.getMessage());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // Get a reminder date by its id
    public RemainderDates getRemainderById(int id) {
        return remainderDatesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + id));
    }

    // Update an existing remainder date
    public RemainderDates updateRemainderDate(int id, RemainderDates remainderDates) {
        RemainderDates existingRemainder = remainderDatesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + id));

        existingRemainder.setDate(remainderDates.getDate());
        existingRemainder.setMessage(remainderDates.getMessage());

        return remainderDatesRepo.save(existingRemainder);
    }

    // Delete a reminder date
    public void deleteRemainderDate(int id) {
        RemainderDates remainderDates = remainderDatesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + id));

        remainderDatesRepo.delete(remainderDates);
    }
}

