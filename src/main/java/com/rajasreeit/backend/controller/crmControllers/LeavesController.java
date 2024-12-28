package com.rajasreeit.backend.controller.crmControllers;

import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.entities.crmEmployeeEntities.CrmLeaves;
import com.rajasreeit.backend.entities.crmEmployeeEntities.LeaveWithEmployeeName;
import com.rajasreeit.backend.service.JwtService;
import com.rajasreeit.backend.service.crmEmployeeServices.LeavesService;
import com.twilio.twiml.voice.Leave;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("crm/employee")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.21:8081/"})
public class LeavesController {

    @Autowired
    private LeavesService leavesService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private JwtService jwtUtils;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    // Endpoint to create a leave request
    @PostMapping("/add-leave")
    public ResponseEntity<CrmLeaves> addLeaveRequest(@RequestBody CrmLeaves leaveRequest, HttpServletRequest request) {
        try {
            CrmLeaves createdLeave = leavesService.addLeaveRequest(leaveRequest);

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("JWT Token is missing or invalid");
            }

            String token = authorizationHeader.substring(7);
            String mobileNumber = jwtUtils.extractUsername(token);

            String message = "Request for leave with mobile number " + mobileNumber + "applied for a leave on " + LocalDateTime.now();

            mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);
            return new ResponseEntity<>(createdLeave, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/with-employee-name")
    public ResponseEntity<List<LeaveWithEmployeeName>> getLeavesWithEmployeeName(HttpServletRequest request) {
        List<LeaveWithEmployeeName> leaves = leavesService.getLeavesWithEmployeeName();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token);

        String message = "Employee with mobile number " + mobileNumber + "retrieved leave on " + LocalDateTime.now();
        activityLogService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message);
        return ResponseEntity.ok(leaves);
    }

}
