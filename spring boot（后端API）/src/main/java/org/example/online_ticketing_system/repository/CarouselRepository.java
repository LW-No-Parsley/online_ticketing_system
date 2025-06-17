package org.example.online_ticketing_system.repository;

import java.util.List;

import org.example.online_ticketing_system.model.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CarouselRepository extends JpaRepository<Carousel, Long> {
    @Query("SELECT c FROM Carousel c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<Carousel> findAllActive();
}
