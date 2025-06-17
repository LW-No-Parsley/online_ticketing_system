package org.example.online_ticketing_system.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.online_ticketing_system.model.Carousel;
import org.example.online_ticketing_system.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/carousel")
public class AdminCarouselController {
    @Autowired
    private CarouselRepository carouselRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/carousel/";

    @GetMapping
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic"})
    public ResponseEntity<List<Carousel>> getAllCarousels() {
        return ResponseEntity.ok(carouselRepository.findAll());
    }

    @PostMapping
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic"})
    public ResponseEntity<?> createCarousel(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam(defaultValue = "true") boolean isActive,
            @RequestParam(defaultValue = "0") int displayOrder) throws IOException {
        
        // 确保上传目录存在
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        
        // 生成唯一文件名
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        Carousel carousel = new Carousel();
        carousel.setImageUrl("/uploads/carousel/" + fileName);
        carousel.setTitle(title);
        carousel.setActive(isActive);
        carousel.setDisplayOrder(displayOrder);
        carousel.setCreateTime(LocalDateTime.now());
        carousel.setUpdateTime(LocalDateTime.now());

        return ResponseEntity.ok(carouselRepository.save(carousel));
    }

    @PutMapping("/{id}")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic"})
    public ResponseEntity<?> updateCarousel(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer displayOrder) throws IOException {
        
        return carouselRepository.findById(id).map(carousel -> {
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + fileName);
                    Files.write(filePath, file.getBytes());
                    carousel.setImageUrl("/uploads/carousel/" + fileName);
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("文件上传失败");
                }
            }
            if (title != null) carousel.setTitle(title);
            if (isActive != null) carousel.setActive(isActive);
            if (displayOrder != null) carousel.setDisplayOrder(displayOrder);
            carousel.setUpdateTime(LocalDateTime.now());
            return ResponseEntity.ok(carouselRepository.save(carousel));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic"})
    public ResponseEntity<?> deleteCarousel(@PathVariable Long id) {
        return carouselRepository.findById(id).map(carousel -> {
            carouselRepository.delete(carousel);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
