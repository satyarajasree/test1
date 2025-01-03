package com.rajasreeit.backend.controller;


import com.rajasreeit.backend.activityLogs.ActivityLogService;
import com.rajasreeit.backend.activityLogs.MobileApplogs;
import com.rajasreeit.backend.activityLogs.MobileApplogsService;
import com.rajasreeit.backend.entities.Holidays;
import com.rajasreeit.backend.repo.DepartmentRepo;
import com.rajasreeit.backend.service.HolidayService;
import com.rajasreeit.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/crm/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private JwtService jwtUtils;

    @Autowired
    private MobileApplogsService mobileApplogsService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/holidays")
    public ResponseEntity<List<Holidays>> getAllHolidays(){
        List<Holidays> holidays = holidayService.findAllHolidays();
        return ResponseEntity.ok(holidays);
    }


    @PostMapping("/post-holiday")
    public ResponseEntity<String> addHoliday(
            @RequestParam("holidayDate") String holidayDate,
            @RequestParam("reasonForHoliday") String reasonForHoliday,
            @RequestParam("departmentId") Integer departmentId, HttpServletRequest request) {

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(holidayDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format.");
        }

        Holidays holiday = holidayService.addHoliday(parsedDate, reasonForHoliday, departmentId);

        String message = departmentRepo.findById(departmentId).isPresent()
                ? "Holiday created successfully."
                : "Holiday created successfully, but department with ID " + departmentId + " not found. No department linked.";

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String mobileNumber = jwtUtils.extractUsername(token);

        String message1 = "Holiday has been declared on "+parsedDate+" with reason "+ reasonForHoliday;
        mobileApplogsService.logActivity(request.getRequestURI(), request.getMethod(), mobileNumber, message1);

        return ResponseEntity.ok(message);
    }


    @GetMapping("/get-holiday/{id}")
    public ResponseEntity<Holidays> getHolidayById(@PathVariable int id) {
        Optional<Holidays> holiday = holidayService.getHolidayById(id);

        return holiday.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Endpoint to update holiday and its department details
    @PutMapping("/holiday/{id}")
    public ResponseEntity<Holidays> updateHoliday(@PathVariable int id, @RequestBody Holidays updatedHoliday) {
        try {
            Holidays holiday = holidayService.updateHoliday(id, updatedHoliday);
            return ResponseEntity.ok(holiday);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Delete Holiday
    @DeleteMapping("/holiday/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable int id) {
        try {
            holidayService.deleteHoliday(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
