package org.example.online_ticketing_system.controller;

import org.example.online_ticketing_system.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carousel")
public class CarouselController {
    @Autowired
    private CarouselRepository carouselRepository;

    @GetMapping
    public ResponseEntity<?> getActiveCarousels() {
        return ResponseEntity.ok(carouselRepository.findAllActive());
    }
}
