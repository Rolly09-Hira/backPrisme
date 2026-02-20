package com.prisme.back.service;

import com.prisme.back.dto.FormationDTO;
import com.prisme.back.entity.Formation;
import com.prisme.back.repository.FormationRepository;
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

@Service
@RequiredArgsConstructor
public class FormationService {

    private final FormationRepository formationRepository;

    private static final String UPLOAD_DIR = "uploads/images/";
    private static final String URL_PREFIX = "/uploads/images/";

    public FormationDTO createFormation(FormationDTO dto, MultipartFile imageFile) {
        Formation f = new Formation();
        f.setTitre(dto.getTitre());
        f.setDescription(dto.getDescription());
        f.setPrix(dto.getPrix());
        f.setCategorie(dto.getCategorie());
        f.setDuree(dto.getDuree());
        f.setLangue(dto.getLangue());

        // Gestion de l'image
        String imagePath = saveImageIfPresent(imageFile);
        if (imagePath != null) {
            f.setImage(imagePath);
        }

        Formation saved = formationRepository.save(f);
        return mapFormation(saved);
    }

    public FormationDTO updateFormation(Long id, FormationDTO dto, MultipartFile imageFile) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));

        if (dto.getTitre() != null) formation.setTitre(dto.getTitre());
        if (dto.getDescription() != null) formation.setDescription(dto.getDescription());
        if (dto.getPrix() != null) formation.setPrix(dto.getPrix());
        if (dto.getCategorie() != null) formation.setCategorie(dto.getCategorie());
        if (dto.getDuree() != null) formation.setDuree(dto.getDuree());
        if (dto.getLangue() != null) formation.setLangue(dto.getLangue());

        // Gestion de la nouvelle image (remplace l'ancienne si envoyée)
        String imagePath = saveImageIfPresent(imageFile);
        if (imagePath != null) {
            formation.setImage(imagePath);
        }

        Formation updated = formationRepository.save(formation);
        return mapFormation(updated);
    }

    private String saveImageIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Créer le dossier s'il n'existe pas
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Générer un nom unique pour éviter les collisions
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;

            // Chemin physique complet
            String filePath = UPLOAD_DIR + fileName;

            // Sauvegarde du fichier
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // Chemin relatif à retourner (stocké en base et utilisé par le frontend)
            return URL_PREFIX + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Échec de l'upload de l'image : " + e.getMessage(), e);
        }
    }

    public List<FormationDTO> getAllFormations() {
        return formationRepository.findAll().stream()
                .map(this::mapFormation)
                .toList();
    }

    public FormationDTO getFormationById(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));
        return mapFormation(formation);
    }

    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }

    private FormationDTO mapFormation(Formation f) {
        FormationDTO dto = new FormationDTO();
        dto.setId(f.getId());
        dto.setTitre(f.getTitre());
        dto.setDescription(f.getDescription());
        dto.setPrix(f.getPrix());
        dto.setCategorie(f.getCategorie());
        dto.setImage(f.getImage());
        dto.setDuree(f.getDuree());
        dto.setLangue(f.getLangue());
        return dto;
    }
}