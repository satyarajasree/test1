package com.rajasreeit.backend.controller.crmControllers;


import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.dto.CrmEmployeeProfileDto;
import com.rajasreeit.backend.entities.CrmEmployee;
import com.rajasreeit.backend.entities.crmEmployeeEntities.BankAccounts;
import com.rajasreeit.backend.imagesS3.S3FileUploadService;
import com.rajasreeit.backend.service.JwtService;
import com.rajasreeit.backend.service.crmEmployeeServices.BankService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/crm/employee")
public class BankController {

    @Autowired
    private BankService bankService;

    @Autowired
    private S3FileUploadService s3FileUploadService;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @Autowired
    private JwtService jwtUtils;

   @PutMapping("/update-images/{id}")
public ResponseEntity<CrmEmployee> editCrmEmployeeProfile(
        @PathVariable int id,
        @RequestParam(value = "profileImagePath", required = false) MultipartFile profileImage,
        @RequestParam(value = "idCardPath", required = false) MultipartFile idCard,
        HttpServletRequest request
) {
    try {
        // Define S3 subdirectory and file names only if the files are present
        String profileImagePath = null;
        String idCardPath = null;

        // Check if profile image is provided
        if (profileImage != null && !profileImage.isEmpty()) {
            // Generate a unique file name using UUID and original file extension
            String profileImageKey = "updated-profile-images/" + UUID.randomUUID().toString() + "-" + profileImage.getOriginalFilename();
            profileImagePath = s3FileUploadService.uploadFile("employees", profileImage, profileImageKey);
        }

        // Check if ID card image is provided
        if (idCard != null && !idCard.isEmpty()) {
            // Generate a unique file name using UUID and original file extension
            String idCardKey = "updated-id-cards/" + UUID.randomUUID().toString() + "-" + idCard.getOriginalFilename();
            idCardPath = s3FileUploadService.uploadFile("employees", idCard, idCardKey);
        }

        // Create DTO for updating employee profile
        CrmEmployeeProfileDto crmEmployeeProfileDto = new CrmEmployeeProfileDto();
        if (profileImagePath != null) {
            crmEmployeeProfileDto.setProfileImagePath(profileImagePath);
        }
        if (idCardPath != null) {
            crmEmployeeProfileDto.setIdCardPath(idCardPath);
        }

        // Update employee in the database
        CrmEmployee updatedEmployee = bankService.updateCrmProfile(id, crmEmployeeProfileDto);

        // Log the activity (optional)
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token); // Extract mobile number from the token

        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, "You have updated your documents " + LocalDateTime.now());

        return ResponseEntity.ok(updatedEmployee);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

    @GetMapping("/get-employee/{id}")
    public ResponseEntity<CrmEmployee> getEmployeeById(@PathVariable int id) {
        CrmEmployee employee = bankService.getEmployee(id);
        return ResponseEntity.ok(employee);
    }


    // Get bank accounts by employee id
    @GetMapping("/bank/{employeeId}")
    public ResponseEntity<List<BankAccounts>> getBankAccountsByEmployeeId(@PathVariable int employeeId) {
        List<BankAccounts> bankAccounts = bankService.getBankAccountsByEmployeeId(employeeId);
        return ResponseEntity.ok(bankAccounts);
    }

    @PostMapping("/add-bankAccount")
    public ResponseEntity<BankAccounts> addBankAccount(@RequestBody BankAccounts bankAccount) {
        BankAccounts createdAccount = bankService.addBankAccount(bankAccount);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/update-bank/{id}")
    public ResponseEntity<BankAccounts> getBankAccountById(@PathVariable int id) {
        BankAccounts bankAccount = bankService.getBankAccountById(id);
        return ResponseEntity.ok(bankAccount);
    }

}
