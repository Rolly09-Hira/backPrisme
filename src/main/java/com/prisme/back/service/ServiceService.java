package com.prisme.back.service;

import com.prisme.back.dto.ServiceDTO;
import com.prisme.back.entity.OffreService;
import com.prisme.back.repository.OffreServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final OffreServiceRepository repository;

    private static final String UPLOAD_DIR = "uploads/images/";
    private static final String URL_PREFIX = "/uploads/images/";

    public ServiceDTO createService(ServiceDTO dto, MultipartFile imageFile) {
        OffreService entity = new OffreService();
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());

        // Gestion de l'image
        String imagePath = saveImageIfPresent(imageFile);
        if (imagePath != null) {
            entity.setImage(imagePath);
        }

        OffreService saved = repository.save(entity);
        return mapToDto(saved);
    }

    public ServiceDTO updateService(Long id, ServiceDTO dto, MultipartFile imageFile) {
        OffreService entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre de service introuvable avec l'id : " + id));

        if (dto.getNom() != null) {
            entity.setNom(dto.getNom());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        // Si nouvelle image → on remplace
        String imagePath = saveImageIfPresent(imageFile);
        if (imagePath != null) {
            entity.setImage(imagePath);
        }

        OffreService updated = repository.save(entity);
        return mapToDto(updated);
    }

    private String saveImageIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            String filePath = UPLOAD_DIR + fileName;

            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            return URL_PREFIX + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Échec de l'upload de l'image : " + e.getMessage(), e);
        }
    }

    public List<ServiceDTO> getAllServices() {
        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(Long id) {
        OffreService entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre de service introuvable avec l'id : " + id));
        return mapToDto(entity);
    }

    public void deleteService(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Offre de service introuvable avec l'id : " + id);
        }
        repository.deleteById(id);
    }

    private ServiceDTO mapToDto(OffreService entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());   // ← maintenant contient /uploads/images/xxx.jpg
        return dto;
    }
}