package com.kpi.demo.repository;

import com.kpi.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SessionRepository extends JpaRepository<Session, Long> {
}
