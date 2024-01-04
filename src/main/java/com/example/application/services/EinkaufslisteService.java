package com.example.application.services;

import com.example.application.data.Einkaufsliste;
import com.example.application.data.EinkaufslisteRepository;
import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;


import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EinkaufslisteService {

    private final EinkaufslisteRepository repository;

	private final AuthenticatedUser authenticatedUser;

	@Autowired
	public EinkaufslisteService(EinkaufslisteRepository repository, AuthenticatedUser authenticatedUser) {
	    this.repository = repository;
	    this.authenticatedUser = authenticatedUser;
	}
    
    
    @Transactional
    public List<Einkaufsliste> getEinkaufslisten() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Einkaufsliste> getEinkaufslisteById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public void createEinkaufsliste(String name, String notizen) {
        // Hole den aktuellen eingeloggten Benutzer
        Optional<User> authenticatedUserOptional = authenticatedUser.get();
        
        if (authenticatedUserOptional.isPresent()) {
            User creator = authenticatedUserOptional.get();

            Einkaufsliste einkaufsliste = new Einkaufsliste(name, LocalDate.now(), false, notizen, false, creator);
            repository.save(einkaufsliste);
        } else {
            // Handle the case when the user is not authenticated
            // You can throw an exception or log a message
        }
    }


    @Transactional
    public void markAsErledigt(Long einkaufslisteId, boolean erledigt) {
        repository.findById(einkaufslisteId).ifPresent(einkaufsliste -> {
            einkaufsliste.setErledigt(erledigt);
            repository.save(einkaufsliste);
        });
    }

    @Transactional
    public Page<Einkaufsliste> listEinkaufslisten(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Page<Einkaufsliste> listEinkaufslisten(Pageable pageable, Specification<Einkaufsliste> filter) {
        return repository.findAll(filter, pageable);
    }

    @Transactional
    public long countEinkaufslisten() {
        return repository.count();
    }

    @Transactional
    public void deleteEinkaufsliste(Long id) {
        repository.deleteById(id);
    }
}