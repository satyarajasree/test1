package com.rajasreeit.backend.controller.crmControllers;

import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.dto.RemainderGetDto;
import com.rajasreeit.backend.entities.crmEmployeeEntities.RemainderDates;
import com.rajasreeit.backend.service.JwtService;
import com.rajasreeit.backend.service.crmEmployeeServices.RemainderDatesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("crm/employee")
public class RemainderDatesController {

    @Autowired
    private RemainderDatesService remainderDatesService;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private JwtService jwtUtils;

    // Controller method to handle creating a new reminder for the authenticated user
    @PostMapping("/create-remainder")
    public ResponseEntity<RemainderDates> createRemainder(HttpServletRequest request, @RequestBody RemainderDates remainderDates) {
        // Call the service method to create a remainder date for the authenticated user
        RemainderDates createdRemainder = remainderDatesService.createRemainderDateForAuthenticatedUser(remainderDates);

        // Log the activity (optional)
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token); // Extract mobile number from the token

        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, "You have added a reminder date " + LocalDateTime.now());

        return ResponseEntity.ok(createdRemainder);
    }


    @GetMapping("/employee/remainders")
    public ResponseEntity<List<RemainderDates>> getAllRemaindersByAuthenticatedUser() {
        try {
            // Fetch all reminders for the authenticated user
            List<RemainderDates> remainders = remainderDatesService.getAllRemaindersByAuthenticatedUser();
            return ResponseEntity.ok(remainders);
        } catch (RuntimeException ex) {
            // Return an error response if something goes wrong
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Controller method to get all remainder dates for the authenticated user
    @GetMapping("/employees/remainders")
    public ResponseEntity<List<RemainderGetDto>> getAllRemainderDatesByAuthenticatedUser(HttpServletRequest request) {
        // Extract token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token);

        // Log activity for auditing (optional)
        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, "Fetching remainder dates");

        // Fetch remainder dates for the authenticated user (based on mobile number)
        try {
            List<RemainderGetDto> remainderGetDtos = remainderDatesService.getAllRemainderDatesByAuthenticatedUser();
            return ResponseEntity.ok(remainderGetDtos);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Get a reminder by its id
    @GetMapping("/{id}")
    public ResponseEntity<RemainderDates> getRemainderById(@PathVariable int id) {
        RemainderDates remainder = remainderDatesService.getRemainderById(id);
        return ResponseEntity.ok(remainder);
    }

    // Update an existing reminder by id
    @PutMapping("/{id}")
    public ResponseEntity<RemainderDates> updateRemainder(@PathVariable int id, @RequestBody RemainderDates remainderDates) {
        RemainderDates updatedRemainder = remainderDatesService.updateRemainderDate(id, remainderDates);
        return ResponseEntity.ok(updatedRemainder);
    }

    // Delete a reminder by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRemainder(@PathVariable int id) {
        remainderDatesService.deleteRemainderDate(id);
        return ResponseEntity.ok("Reminder deleted successfully");
    }
}
