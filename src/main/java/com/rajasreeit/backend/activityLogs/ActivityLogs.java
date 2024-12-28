package com.rajasreeit.backend.activityLogs;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String apiUrl;
    private String requestMethod;
    private String mobileNumber;
    private String message; // Custom log message
    private LocalDateTime timestamp;
}
