package org.example.online_ticketing_system.repository;

import java.util.List;
import java.util.Optional;

import org.example.online_ticketing_system.model.TicketInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketInventoryRepository extends JpaRepository<TicketInventory, Long> {
    Optional<TicketInventory> findByDateAndTime(String date, String time);

    List<TicketInventory> findByDateGreaterThanEqualOrderByDateAsc(String date);

    @Query("SELECT t FROM TicketInventory t WHERE t.date IN :dates")
    List<TicketInventory> findByDateIn(@Param("dates") List<String> dates);
}
