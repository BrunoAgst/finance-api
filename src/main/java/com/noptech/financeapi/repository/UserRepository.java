package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {}
