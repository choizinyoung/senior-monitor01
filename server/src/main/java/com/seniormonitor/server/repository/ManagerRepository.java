package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Manager> findAllByOrderByCreatedAtDesc();

    List<Manager> findAllByStatusOrderByCreatedAtDesc(String status);
}
