package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EinkaufslisteRepository extends JpaRepository<Einkaufsliste, Long>, JpaSpecificationExecutor<Einkaufsliste> {

    Einkaufsliste findByid(Integer id);
    
    
}

