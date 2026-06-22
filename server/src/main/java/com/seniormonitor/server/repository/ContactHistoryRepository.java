package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.ContactHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactHistoryRepository extends JpaRepository<ContactHistory, Long> {

    List<ContactHistory> findBySeniorIdOrderByContactedAtDesc(Long seniorId);
}
