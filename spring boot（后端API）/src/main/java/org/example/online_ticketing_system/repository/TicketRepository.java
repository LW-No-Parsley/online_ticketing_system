package org.example.online_ticketing_system.repository;

import java.util.Optional;

import org.example.online_ticketing_system.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByDateAndTime(String date, String time);
}
