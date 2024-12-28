package com.rajasreeit.backend.controller.crmControllers;

import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.dto.EnquiryWithEmployeeName;
import com.rajasreeit.backend.entities.crmEmployeeEntities.Enquiry;
import com.rajasreeit.backend.service.JwtService;
import com.rajasreeit.backend.service.crmEmployeeServices.EnquiryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/crm/employee")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private JwtService jwtUtils;

    @PostMapping("/add-enquiry")
    public ResponseEntity<Enquiry> addEnquiry(@RequestBody Enquiry enquiry, HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token);

        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, "You have raised a new enquiry on "+ LocalDateTime.now());
        System.out.println("logs printing");
        return ResponseEntity.ok(enquiryService.addEnquiry(enquiry));
    }


    @GetMapping("/enquiries")
    public ResponseEntity<List<Enquiry>> getEnquiries() {
        return ResponseEntity.ok(enquiryService.getEnquiriesForAuthenticatedEmployee());
    }



    @GetMapping("/enquiries-with-names")
    public ResponseEntity<List<EnquiryWithEmployeeName>> getEnquiriesWithEmployeeName() {
        return ResponseEntity.ok(enquiryService.getEnquiriesWithEmployeeName());
    }
}
