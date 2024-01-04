package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "einkaufsliste")
public class Einkaufsliste extends AbstractEntity {

    private String name;
    private LocalDate erstelldatum;
    private boolean erledigt;
    private String notizen;
    private boolean geteilt;
   

    
    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;
    
    // Konstruktor
    public Einkaufsliste(String name, LocalDate erstelldatum, boolean erledigt, String notizen, boolean geteilt, User creator) {
        this.name = name;
        this.erstelldatum = erstelldatum;
        this.erledigt = erledigt;
        this.notizen = notizen;
        this.geteilt = geteilt;
        this.creator = creator;
    }

   
    public Einkaufsliste() {
    }

    
    
    // Getter und Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getErstelldatum() {
        return erstelldatum;
    }

    public void setErstelldatum(LocalDate erstelldatum) {
        this.erstelldatum = erstelldatum;
    }

    public boolean isErledigt() {
        return erledigt;
    }

    public void setErledigt(boolean erledigt) {
        this.erledigt = erledigt;
    }

	public String getNotizen() {
		return notizen;
	}

	public void setNotizen(String notizen) {
		this.notizen = notizen;
	}


	public boolean isGeteilt() {
		return geteilt;
	}


	public void setGeteilt(boolean geteilt) {
		this.geteilt = geteilt;
	}


	public User getCreator() {
		return creator;
	}


	public void setCreator(User creator) {
		this.creator = creator;
	}

}