package com.example.application.views.einkaufsliste;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.application.data.AbstractEntity;
import com.example.application.data.Einkaufsliste;
import com.example.application.data.EinkaufslisteRepository;
import com.example.application.data.User;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

//TODO Add User filtered lists (private, public being the way they are supposed to so users only see their corresponding lists)


@PageTitle("Einkaufsliste")
@Route(value = "einkaufsliste", layout = MainLayout.class)
@AnonymousAllowed
public class EinkaufslisteView extends VerticalLayout {

    private static final long serialVersionUID = -194832421052470355L;
    
        
    private Authentication authentication;
	private List<Einkaufsliste> einkaufslisten;
    private EinkaufslisteRepository einkaufslisteRepository;
    private Grid<Einkaufsliste> grid;
    private Integer creatorId;
    

    private ComboBox<String> erledigtFilter;
    private ComboBox<String> sichtbarkeitFilter;
    
    public EinkaufslisteView(EinkaufslisteRepository einkaufslisteRepository) {
        this.einkaufslisteRepository = einkaufslisteRepository;
        createHeader();
        this.grid = new Grid<>(); // Grid zuerst initialisieren
        createGrid(); // Dann die Grid-Konfiguration vornehmen
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        
        
        
        
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Header-Text
        Span headerText = new Span("Einkaufsliste");
        headerText.getStyle().set("font-size", "2em"); // Ändern Sie die Schriftgröße nach Bedarf
        headerText.getStyle().set("font-weight", "bold"); // Fett anzeigen
        headerText.getStyle().set("text-shadow", "2px 2px 4px rgba(0, 0, 0, 0.2)"); // Fügen Sie einen Schatten hinzu

        header.add(headerText);

        erledigtFilter = new ComboBox<>("");
        erledigtFilter.setItems("Alle", "Erledigt", "Offen");
        erledigtFilter.setValue("Alle");
        erledigtFilter.addValueChangeListener(event -> applyFilters());

        header.add(erledigtFilter);

        sichtbarkeitFilter = new ComboBox<>("");
        sichtbarkeitFilter.setItems("Alle", "Geteilt", "Privat");
        sichtbarkeitFilter.setValue("Alle");
        sichtbarkeitFilter.addValueChangeListener(event -> applyFilters());

        header.add(sichtbarkeitFilter);

        // Button "Neue Einkaufsliste erstellen"
        Button addButton = new Button("Neue Einkaufsliste erstellen", VaadinIcon.PLUS.create());
        addButton.addClickListener(e -> openCreateEinkaufslisteDialog());
        header.add(addButton);

        add(header);
    }
    


    private void createGrid() {
        // Einkaufslisten vom Repository laden
        einkaufslisten = einkaufslisteRepository.findAll();

        // Spalten für die Einkaufsliste
        grid.addColumn(einkaufsliste -> einkaufsliste.isGeteilt() ? "Geteilt" : "Privat")
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Einkaufsliste::getName)
                .setHeader("Einkaufsliste")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(Einkaufsliste::getErstelldatum)
                .setHeader("Erstelldatum")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(Einkaufsliste::getNotizen)
                .setHeader("Notizen")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(einkaufsliste -> {
            // Erstelle ein Checkbox-Element basierend auf dem "Erledigt"-Status
            Checkbox erledigtCheckbox = new Checkbox(einkaufsliste.isErledigt());
            erledigtCheckbox.addValueChangeListener(event -> {
                einkaufsliste.setErledigt(event.getValue());
                einkaufslisteRepository.save(einkaufsliste);
                updateGrid();
            });

            return erledigtCheckbox;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0);

        grid.addColumn(createActionsRenderer())
                .setHeader("")
                .setFlexGrow(0);

        // Einkaufslisten zur Grid hinzufügen
        grid.setItems(einkaufslisten);

        // Grid in ein Container-Layout einbetten
        HorizontalLayout gridContainer = new HorizontalLayout(grid);
        gridContainer.setSizeFull();
        gridContainer.expand(grid); // Expand to take all available space

        // Das Container-Layout zur Hauptlayout hinzufügen
        add(grid); 
        setFlexGrow(1, grid); 
        setSizeFull();
    }
    
    private void updateGrid() {
        System.out.println("updateGrid() called");
        einkaufslisten = einkaufslisteRepository.findAll();
        grid.setItems(einkaufslisten);
        System.out.println("Grid updated with " + einkaufslisten.size() + " items");
    }
    
    private ComponentRenderer<HorizontalLayout, Einkaufsliste> createActionsRenderer() {
        return new ComponentRenderer<>(einkaufsliste -> {
            Button editButton = new Button(VaadinIcon.PENCIL.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editButton.getStyle().set("border-radius", "0");

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.getStyle().set("border-radius", "0");

            editButton.addClickListener(event -> openEditEinkaufslisteDialog(einkaufsliste, null));
            
            deleteButton.addClickListener(event -> deleteEinkaufsliste(einkaufsliste));

            HorizontalLayout actionsLayout = new HorizontalLayout(editButton, deleteButton);
            actionsLayout.setSpacing(true);

            return actionsLayout;
        });
    }
    
    private void openEditEinkaufslisteDialog(Einkaufsliste einkaufsliste, User user) {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("500px");

        TextField editedNameField = new TextField("Geänderte Einkaufslisten-Name");
        editedNameField.setValue(einkaufsliste.getName());
        editedNameField.setWidthFull();

        TextArea editedNoteField = new TextArea("Geänderte Notizen");
        editedNoteField.setValue(einkaufsliste.getNotizen() != null ? einkaufsliste.getNotizen() : "");
        editedNoteField.setWidthFull();
        editedNoteField.setHeight("150px"); // Set the desired height for the note field

        Checkbox erledigtCheckbox = new Checkbox("Erledigt");
        erledigtCheckbox.setValue(einkaufsliste.isErledigt());

        ComboBox<String> sichtbarkeitComboBox = new ComboBox<>("");
        sichtbarkeitComboBox.setItems("Privat", "Geteilt");
        sichtbarkeitComboBox.setValue(einkaufsliste.isGeteilt() ? "Geteilt" : "Privat");

        Button saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {
            String editedName = editedNameField.getValue();

            if (editedName.trim().isEmpty()) {
                showError("Du musst einen Namen eingeben");
                return;
            }

            einkaufsliste.setName(editedName);
            einkaufsliste.setNotizen(editedNoteField.getValue());
            einkaufsliste.setErledigt(erledigtCheckbox.getValue());
            einkaufsliste.setGeteilt("Geteilt".equals(sichtbarkeitComboBox.getValue()));
            //TODO last change UserId?
            einkaufslisteRepository.save(einkaufsliste);
            updateGrid();
            editDialog.close();
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickListener(event -> editDialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setSpacing(true);

        VerticalLayout editDialogContent = new VerticalLayout(editedNameField, editedNoteField, erledigtCheckbox, sichtbarkeitComboBox, buttonLayout);
        editDialogContent.setPadding(true);
        editDialogContent.setSpacing(true);

        editDialog.add(editDialogContent);
        editDialog.open();
    }
    
    
    private void deleteEinkaufsliste(Einkaufsliste einkaufsliste) {
        Dialog confirmDeleteDialog = new Dialog();
        confirmDeleteDialog.setWidth("500px");

        Span confirmLabel = new Span("Möchten Sie diese Einkaufsliste wirklich löschen?");
        confirmLabel.getStyle().set("font-weight", "bold");

        Button deleteButton = new Button("Löschen");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> {
            // Hier die Logik zum Löschen der Einkaufsliste
            einkaufslisteRepository.delete(einkaufsliste);
            updateGrid(); // Grid aktualisieren
            confirmDeleteDialog.close();
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickListener(event -> confirmDeleteDialog.close());


        
        VerticalLayout confirmDeleteDialogContent = new VerticalLayout(confirmLabel, deleteButton, cancelButton);
        confirmDeleteDialogContent.setPadding(true);
        confirmDeleteDialogContent.setSpacing(true);

        confirmDeleteDialog.add(confirmDeleteDialogContent);
        confirmDeleteDialog.open();
    }

    private void openCreateEinkaufslisteDialog() {
        // Dialog für die Einkaufsliste-Erstellung erstellen
    	
    	
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        // Textfeld für den Einkaufslisten-Namen
        TextField einkaufslisteNameField = new TextField("Einkaufslisten-Namen");
        einkaufslisteNameField.setWidthFull();

        // TextArea für die Notizen
        TextArea einkaufslisteNoteField = new TextArea("Notizen");
        einkaufslisteNoteField.setWidthFull();
        einkaufslisteNoteField.setHeight("150px"); // Set the desired height for the note field

        // Dropdown-Menü für die Sichtbarkeit
        ComboBox<String> sichtbarkeitComboBox = new ComboBox<>("Sichtbarkeit");
        sichtbarkeitComboBox.setItems("Privat", "Geteilt");
        sichtbarkeitComboBox.setValue("Privat"); // Standardwert
        sichtbarkeitComboBox.setWidthFull();

        // Button zum Bestätigen und Schließen des Dialogs
        Button confirmButton = new Button("Erstellen");
        confirmButton.addClickListener(event -> createEinkaufsliste(dialog, einkaufslisteNameField, einkaufslisteNoteField, sichtbarkeitComboBox, null));

        // Überwachung der Enter-Taste im Textfeld
        einkaufslisteNameField.addKeyPressListener(Key.ENTER, keyPressEvent -> {
			createEinkaufsliste(dialog, einkaufslisteNameField, einkaufslisteNoteField, sichtbarkeitComboBox, null);
        });

        // Button zum Abbrechen und Schließen des Dialogs
        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickListener(event -> dialog.close());

        // Layout für die Dialog-Inhalte
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);

        VerticalLayout dialogContent = new VerticalLayout(einkaufslisteNameField, einkaufslisteNoteField, sichtbarkeitComboBox, buttonLayout);
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);

        // Dialog-Inhalte zum Dialog hinzufügen
        dialog.add(dialogContent);

        // Dialog anzeigen
        dialog.open();
    }
    
    private void showError(String errorMessage) {
        // Dialog für die Fehlermeldung erstellen
        Dialog errorDialog = new Dialog();
        errorDialog.setWidth("300px");

        // Text für die Fehlermeldung
        Span errorLabel = new Span(errorMessage);
        errorLabel.getStyle().set("color", "red");

        // Button zum Schließen des Dialogs
        Button closeButton = new Button("OK");
        closeButton.addClickListener(event -> errorDialog.close());

        // Layout für die Dialog-Inhalte
        VerticalLayout errorDialogContent = new VerticalLayout(errorLabel, closeButton);
        errorDialogContent.setPadding(true);
        errorDialogContent.setSpacing(true);

        // Dialog-Inhalte zum Dialog hinzufügen
        errorDialog.add(errorDialogContent);

        // Dialog anzeigen
        errorDialog.open();
    }
    
    private void applyFilters() {
        // Lade Einkaufslisten vom Repository
        einkaufslisten = einkaufslisteRepository.findAll();

        // Filter anwenden
        String erledigtFilterValue = erledigtFilter.getValue();
        String sichtbarkeitFilterValue = sichtbarkeitFilter.getValue();

        // Filter für erledigte und offene Einkaufslisten
        if (!erledigtFilterValue.equals("Alle")) {
            boolean erledigt = erledigtFilterValue.equals("Erledigt");
            einkaufslisten = einkaufslisten.stream()
                    .filter(einkaufsliste -> einkaufsliste.isErledigt() == erledigt)
                    .collect(Collectors.toList());
        }

        // Filter für geteilte und private Einkaufslisten
        if (!sichtbarkeitFilterValue.equals("Alle")) {
            boolean geteilt = sichtbarkeitFilterValue.equals("Geteilt");
            einkaufslisten = einkaufslisten.stream()
                    .filter(einkaufsliste -> einkaufsliste.isGeteilt() == geteilt)
                    .collect(Collectors.toList());
        }

        // Grid aktualisieren
        grid.setItems(einkaufslisten);
    }
    

    
    private void createEinkaufsliste(Dialog dialog, TextField einkaufslisteNameField, TextArea einkaufslisteNoteField, ComboBox<String> sichtbarkeitComboBox, User creator) {
        // Hier können Sie die Logik für die Erstellung der Einkaufsliste implementieren
        String einkaufslisteName = einkaufslisteNameField.getValue();
        String einkaufslisteNotes = einkaufslisteNoteField.getValue();
        boolean isGeteilt = sichtbarkeitComboBox.getValue().equals("Geteilt");

        if (einkaufslisteName.trim().isEmpty()) {
            // Show an error message
            showError("Du musst einen Namen eingeben");
            return;
        }

        // Create a new Einkaufsliste with the creator_id
        Einkaufsliste neueEinkaufsliste = new Einkaufsliste(einkaufslisteName, LocalDate.now(), false, einkaufslisteNotes, isGeteilt, creator);

        // Einkaufsliste im Repository speichern
        einkaufslisteRepository.save(neueEinkaufsliste);

        // Grid aktualisieren
        updateGrid();

        // Dialog schließen
        dialog.close();
    }
}