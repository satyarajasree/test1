package com.rajasreeit.backend.controller.crmControllers;

import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.dto.PunchActivityDTO;
import com.rajasreeit.backend.entities.crmEmployeeEntities.CrmPunchActivity;
import com.rajasreeit.backend.service.JwtService;
import com.rajasreeit.backend.service.crmEmployeeServices.PunchActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("crm/employee")
public class PunchActivityController {

    @Autowired
    private PunchActivityService punchActivityService;

    @Autowired
    private JwtService jwtUtils;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @PostMapping("punch")
    public ResponseEntity<?> savePunchActivity(
            @RequestParam(value = "punchInImage", required = false) MultipartFile punchInImage,
            @RequestParam(value = "punchOutImage", required = false) MultipartFile punchOutImage,
            @RequestParam(value = "workReport", required = false) String workReport,
            @RequestParam(value = "remainderDate", required = false) String remainderDate,
            HttpServletRequest request) {

        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("JWT Token is missing or invalid");
            }

            String token = authorizationHeader.substring(7);
            String mobileNumber = jwtUtils.extractUsername(token);

            CrmPunchActivity savedPunchActivity = punchActivityService.savePunchActivity(punchInImage, punchOutImage, workReport, remainderDate, mobileNumber);

            String punchType = punchOutImage != null ? "punched out" : "punched in";
            String message = "Dear employee you with mobile number " + mobileNumber + " " + punchType + " on " + LocalDateTime.now();

            mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);
            return ResponseEntity.ok(savedPunchActivity);

        } catch (IllegalStateException e) {
            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), null, "Punch activity failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), null, "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/punch-activities")
    public ResponseEntity<List<PunchActivityDTO>> getPunchActivities(HttpServletRequest request) {
        try {
            List<PunchActivityDTO> punchActivities = punchActivityService.getPunchActivities();
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("JWT Token is missing or invalid");
            }

            String token = authorizationHeader.substring(7);
            String mobileNumber = jwtUtils.extractUsername(token);

            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, "Retrieved punch activities for the authenticated user.");
            System.out.println("logs printing");


            return ResponseEntity.ok(punchActivities);
        } catch (RuntimeException e) {
            activityLogService.logActivity(request.getRequestURI(), request.getMethod(), null, "Failed to retrieve punch activities: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
