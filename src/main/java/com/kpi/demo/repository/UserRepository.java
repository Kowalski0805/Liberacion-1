package com.kpi.demo.repository;

import com.kpi.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findById(long id);
}