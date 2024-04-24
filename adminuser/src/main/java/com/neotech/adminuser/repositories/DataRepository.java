package com.neotech.adminuser.repositories;

import com.neotech.adminuser.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {
    Data findByDateAndUsername(LocalDate date, String username);
}
