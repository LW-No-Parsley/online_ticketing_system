package org.example.online_ticketing_system.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.online_ticketing_system.model.TicketInventory;
import org.example.online_ticketing_system.repository.TicketInventoryRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
public class TicketDataController {

    @Autowired
    private TicketInventoryRepository ticketInventoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 10 17 * * ?") // 每天下午5:10触发
    public void initializeDayAfterTomorrowTickets() {
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

        checkAndCreate(dayAfterTomorrow, "上午");
        checkAndCreate(dayAfterTomorrow, "下午");
    }

    private void checkAndCreate(LocalDate date, String time) {
        boolean exists = ticketInventoryRepository.findByDateAndTime(date.toString(), time).isPresent();
        if (!exists) {
            TicketInventory ticket = new TicketInventory();
            ticket.setDate(date.toString());
            ticket.setTime(time);
            ticket.setTotalTickets(500);
            ticket.setSoldTickets(0);
            ticket.setStatus(1); // 可预订
            ticketInventoryRepository.save(ticket);
        }
    }

    @GetMapping("/api/tickets/initialize")
    public String manualInitialize() {
        initializeDayAfterTomorrowTickets();
        return "Ticket initialization for day after tomorrow triggered successfully";
    }
}