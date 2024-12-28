package com.rajasreeit.backend.activityLogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepo extends JpaRepository<ActivityLogs, Integer> {
}
