package org.example.online_ticketing_system.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.example.online_ticketing_system.model.TicketInventory;
import org.example.online_ticketing_system.repository.TicketInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TicketStatusController {

    @Autowired
    private TicketInventoryRepository ticketInventoryRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/status")
    public List<TicketInventory> getTicketStatus() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        
        return ticketInventoryRepository.findByDateIn(
            List.of(
                today.format(DATE_FORMAT),
                tomorrow.format(DATE_FORMAT),
                dayAfterTomorrow.format(DATE_FORMAT)
            )
        );
    }
}
