package com.prisme.back.controller;

import com.prisme.back.dto.ServiceDTO;
import com.prisme.back.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceDTO> createService(
            @RequestPart("service") ServiceDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceService.createService(dto, imageFile));
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @RequestPart("service") ServiceDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(serviceService.updateService(id, dto, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}