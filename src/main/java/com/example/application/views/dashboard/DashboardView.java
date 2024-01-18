package com.example.application.views.dashboard;

import com.example.application.views.MainLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Dashboard")
@Route(value = "Dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class DashboardView extends VerticalLayout {

    private static final long serialVersionUID = 8369787977329015734L;


	public DashboardView() {
        add(createWeekCalendar());
        add(createShoppingListAndEventsLayout());
    }

    private HorizontalLayout createWeekCalendar() {
        HorizontalLayout weekCalendar = new HorizontalLayout();
        weekCalendar.setWidthFull();

        // Bestimmen Sie den Starttag und den Endtag der aktuellen Woche
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Erstellung des Wochengrids - Button zum springen auf die vorherige sowie nächste Woche einfügen
        Grid<LocalDate> weekGrid = new Grid<>();
        weekGrid.setWidthFull();

        // create a List of days as LocalDate
        List<LocalDate> daysOfWeek = new ArrayList<>();
        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            daysOfWeek.add(date);
        }

        // Add a column for each day
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (LocalDate day : daysOfWeek) {
            weekGrid.addColumn(item -> day.format(dayFormatter) + " | " + day.format(dateFormatter))
                    .setHeader(day.format(dayFormatter) + " | " + day.format(dateFormatter));
        }


        weekCalendar.add(weekGrid);

        return weekCalendar;
    }

    private FlexLayout createShoppingListAndEventsLayout() {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();

        // Erstellen Sie das HorizontalLayout für die Einkaufsliste
        HorizontalLayout shoppingListSnippet = createShoppingListSnippet();

        shoppingListSnippet.getElement().getStyle().set("margin-right", "1%");
        
        // Erstellen Sie das HorizontalLayout für die Liste der heutigen Termine
        HorizontalLayout todayEventsList = createTodayEventsList();
        

        // Verwenden Sie FlexLayout, um die beiden HorizontalLayouts nebeneinander anzuzeigen
        layout.add(shoppingListSnippet, todayEventsList);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        return layout;
    }

    private HorizontalLayout createShoppingListSnippet() {
		
    	HorizontalLayout purchaseList = new HorizontalLayout();
    	purchaseList.setWidthFull();
    	purchaseList.setMaxWidth("50%");
    	
    	Grid<String> purchaseListGrid = new Grid<>();
        purchaseListGrid.setWidthFull();
    	
    	List<String> currentPurchaseList = new ArrayList<>();
         
    	purchaseListGrid.addColumn(String::toString).setHeader("Einkaufsliste");
         
    	
    	for (String purchase : currentPurchaseList) {
    		purchaseListGrid.addColumn(item -> purchase).setHeader(purchase);
    	}
    	
    	
    	
    	
    	purchaseList.add(purchaseListGrid);

    	return purchaseList;
    }

    private HorizontalLayout createTodayEventsList() {
        HorizontalLayout todaysEventsListLayout = new HorizontalLayout();
        todaysEventsListLayout.setWidthFull();
        todaysEventsListLayout.setMaxWidth("50%");
        
        Grid<String> todaysEventGrid = new Grid<>();
        todaysEventGrid.setWidthFull();
        
        List<String> todaysEventList = new ArrayList<>();
        
        todaysEventGrid.addColumn(String::toString).setHeader("Heutige Termine");
        
        for (String event : todaysEventList) {
            todaysEventGrid.addColumn(item -> event).setHeader(event);
        }
        
        todaysEventsListLayout.add(todaysEventGrid);
        return todaysEventsListLayout;
    }
}
