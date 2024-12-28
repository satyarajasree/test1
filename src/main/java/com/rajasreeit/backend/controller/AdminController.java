package com.rajasreeit.backend.controller;

import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.ActivityLogs;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.dto.EnquiryWithEmployeeName;
import com.rajasreeit.backend.dto.PasswordChangeRequest;
import com.rajasreeit.backend.entities.CrmAdmin;
import com.rajasreeit.backend.entities.crmEmployeeEntities.*;
import com.rajasreeit.backend.service.CrmAdminService;
import com.rajasreeit.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/crm/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class AdminController {

    @Autowired
    private CrmAdminService crmAdminService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtUtils;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @PostMapping("/register")
    public ResponseEntity<CrmAdmin> registerAdmin(@RequestBody CrmAdmin crmAdmin, HttpServletRequest request) {
        try {
            CrmAdmin registeredAdmin = crmAdminService.register(crmAdmin);
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("JWT Token is missing or invalid");
            }

            String token = authorizationHeader.substring(7);
            String mobileNumber = jwtUtils.extractUsername(token);

            // Log activity for registering admin
            String message = "CRM admin with username " + mobileNumber + " has registered an employee at " + LocalDateTime.now();
            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

            return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CrmAdmin crmAdmin, HttpServletRequest request) {
        try {
            String jwtToken = crmAdminService.verify(crmAdmin);
            if (jwtToken != null) {
                String authorizationHeader = request.getHeader("Authorization");
                String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
                String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

                // Log activity for login
                String message = "CRM admin with username " + mobileNumber + " logged in at " + LocalDateTime.now();
                activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

                return new ResponseEntity<>(jwtToken, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            jwtService.blacklistToken(token);

            String mobileNumber = jwtUtils.extractUsername(token);

            // Log activity for logout
            String message = "CRM admin with username " + mobileNumber + " logged out at " + LocalDateTime.now();
            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

            return ResponseEntity.ok("Logout successful. Token invalidated.");
        }
        return ResponseEntity.badRequest().body("Invalid request. Token missing.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeRequest passwordChangeRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        String response = crmAdminService.changePassword(
                passwordChangeRequest.getOldPassword(),
                passwordChangeRequest.getNewPassword(),
                passwordChangeRequest.getConfirmPassword(),
                token
        );

        // Log activity for password change
        String message = "CRM admin with username " + mobileNumber + " changed their password at " + LocalDateTime.now();
        activityLogService.logActivity("/crm/admin/change-password", "POST", mobileNumber, message);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaves/{status}")
    public List<LeaveWithEmployeeName> getLeavesByStatus(@PathVariable LeavesEnum status, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for getting leaves by status
        String message = "CRM admin with username " + mobileNumber + " viewed leaves with status " + status + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        return crmAdminService.getLeaves(status);
    }

    @PutMapping("/{leaveId}/status")
    public CrmLeaves updateLeavesEnum(@PathVariable int leaveId, @RequestParam LeavesEnum status, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for updating leave status
        String message = "CRM admin with username " + mobileNumber + " updated leave status for leaveId " + leaveId + " to " + status + " at " + LocalDateTime.now();
        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        return crmAdminService.updateLeavesEnum(leaveId, status);
    }

    @GetMapping("/punch/all")
    public ResponseEntity<List<CrmPunchActivity>> getAllPunchActivities(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for getting all punch activities
        String message = "CRM admin with username " + mobileNumber + " viewed all punch activities at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        List<CrmPunchActivity> punchActivities = crmAdminService.getAllPunchActivities();
        return ResponseEntity.ok(punchActivities);
    }

    @GetMapping("/punch/{id}")
    public ResponseEntity<CrmPunchActivity> getPunchActivityById(@PathVariable int id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for getting punch activity by id
        String message = "CRM admin with username " + mobileNumber + " viewed punch activity with id " + id + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        Optional<CrmPunchActivity> punchActivity = crmAdminService.getPunchActivityById(id);
        return punchActivity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/punch/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<CrmPunchActivity> updatePunchActivity(
            @PathVariable int id,
            @RequestParam("date") String date,
            @RequestParam("timeOfPunchIn") String timeOfPunchIn,
            @RequestParam("timeOfPunchOut") String timeOfPunchOut,
            @RequestParam(value = "punchInImage", required = false) MultipartFile punchInImage,
            @RequestParam(value = "punchOutImage", required = false) MultipartFile punchOutImage,
            HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for updating punch activity
        String message = "CRM admin with username " + mobileNumber + " updated punch activity with id " + id + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        try {
            CrmPunchActivity updatedActivity = crmAdminService.updatePunchActivity(
                    id, date, timeOfPunchIn, timeOfPunchOut, punchInImage, punchOutImage);
            return ResponseEntity.ok(updatedActivity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enquiries")
    public ResponseEntity<List<EnquiryWithEmployeeName>> getAllEnquiries(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for viewing all enquiries
        String message = "CRM admin with username " + mobileNumber + " viewed all enquiries at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        List<EnquiryWithEmployeeName> enquiries = crmAdminService.getAllEnquiries();
        return ResponseEntity.ok(enquiries);
    }

    @DeleteMapping("/enquiry/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable int id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for deleting an enquiry
        String message = "CRM admin with username " + mobileNumber + " deleted enquiry with id " + id + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        crmAdminService.deleteEnquiry(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BankAccounts> updateBankAccount(
            @PathVariable int id,
            @RequestBody BankAccounts bankAccount, HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for updating a bank account
        String message = "CRM admin with username " + mobileNumber + " updated bank account with id " + id + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        BankAccounts updatedAccount = crmAdminService.updateBankAccount(id, bankAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping
    public ResponseEntity<List<BankAccounts>> getAllBankAccounts(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for getting all bank accounts
        String message = "CRM admin with username " + mobileNumber + " viewed all bank accounts at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        List<BankAccounts> accounts = crmAdminService.getAllBankAccounts();
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable int id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null;
        String mobileNumber = token != null ? jwtUtils.extractUsername(token) : "unknown";

        // Log activity for deleting a bank account
        String message = "CRM admin with username " + mobileNumber + " deleted bank account with id " + id + " at " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);

        crmAdminService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }

}
