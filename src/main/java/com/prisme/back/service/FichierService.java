package com.prisme.back.service;

import com.prisme.back.dto.FichierDTO;
import com.prisme.back.dto.FormationDTO;
import com.prisme.back.dto.ModuleDTO;
import com.prisme.back.entity.Fichier;
import com.prisme.back.entity.Formation;
import com.prisme.back.entity.Matiere;
import com.prisme.back.entity.Module;
import com.prisme.back.repository.FichierRepository;
import com.prisme.back.repository.FormationRepository;
import com.prisme.back.repository.MatiereRepository;
import com.prisme.back.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FichierService {

    private final FichierRepository fichierRepository;
    private final ModuleRepository moduleRepository;
    private final MatiereRepository matiereRepository;
    private final FormationRepository formationRepository;


    // Chemin de base pour les uploads (relatif à src/main/resources/static/)
    private static final String UPLOAD_DIR = "uploads/";
    private static final Path UPLOAD_PATH = Paths.get("./uploads/").toAbsolutePath().normalize();


    public FichierDTO uploadAndCreateFichier(MultipartFile file, Long moduleId, Long matiereId, Long formationId, String description) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }

        // Générer un nom unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;

        // Déterminer le sous-dossier selon le type
        String type = determineFileType(extension);
        String subDir = switch (type) {
            case "VIDEO" -> "videos/";
            case "IMAGE" -> "photo/";
            case "PDF"   -> "pdf/";
            default      -> "autres/";
        };

        Path subPath = UPLOAD_PATH.resolve(subDir);
        if (!Files.exists(subPath)) {
            Files.createDirectories(subPath);
        }

        Path targetPath = subPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // URL relative accessible via static
        String relativeUrl = "/" + UPLOAD_DIR + subDir + uniqueFilename;

        // Créer l'entité
        Fichier fichier = new Fichier();
        fichier.setNom(originalFilename);
        fichier.setType(type);
        fichier.setUrl(relativeUrl);
        fichier.setTaille(file.getSize());
        fichier.setFormat(extension.substring(1));
        fichier.setDescription(description);

        if (moduleId != null) {
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new RuntimeException("Module non trouvé"));
            fichier.setModule(module);
        }

        if (matiereId != null) {

            Matiere matiere = matiereRepository.findById(matiereId)
                    .orElseThrow(() -> new RuntimeException("Matiere non trouvé"));
            fichier.setMatiere(matiere);

        }

        if (formationId != null) {
            Formation formation = formationRepository.findById(formationId)
                    .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
            fichier.setFormation(formation);
        }

        Fichier saved = fichierRepository.save(fichier);
        return mapFichier(saved);
    }

    public FichierDTO getFichierById(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
        return mapFichier(fichier);
    }

    public List<FichierDTO> getAllFichiers() {
        return fichierRepository.findAll().stream()
                .map(this::mapFichier)
                .toList();
    }

    public FichierDTO updateFichier(Long id, FichierDTO dto) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        if (dto.getNom() != null) fichier.setNom(dto.getNom());
        if (dto.getType() != null) fichier.setType(dto.getType());
        if (dto.getUrl() != null) fichier.setUrl(dto.getUrl());
        if (dto.getTaille() != null) fichier.setTaille(dto.getTaille());
        if (dto.getFormat() != null) fichier.setFormat(dto.getFormat());
        if (dto.getDescription() != null) fichier.setDescription(dto.getDescription());
        if (dto.getModuleId() != null) {
            Module m = moduleRepository.findById(dto.getModuleId()).orElseThrow();
            fichier.setModule(m);
        }
        if (dto.getFormationId() != null) {
            Formation f = formationRepository.findById(dto.getFormationId()).orElseThrow();
            fichier.setFormation(f);
        }

        Fichier updated = fichierRepository.save(fichier);
        return mapFichier(updated);
    }

    public void deleteFichier(Long id) {
        fichierRepository.deleteById(id);
    }

    // Lister les fichiers d'un module
    public List<FichierDTO> getFichiersByModule(Long moduleId) {
        return fichierRepository.findByModuleId(moduleId)   // ← à ajouter dans le repository
                .stream()
                .map(this::mapFichier)
                .toList();
    }

    public List<FichierDTO> getFichiersByMatiere(Long matiereId) {
        return fichierRepository.findByMatiereId(matiereId)
                .stream()
                .map(this::mapFichier)
                .toList();
    }

    // Lister les fichiers d'une formation (optionnel)
    public List<FichierDTO> getFichiersByFormation(Long formationId) {
        return fichierRepository.findByFormationId(formationId)
                .stream()
                .map(this::mapFichier)
                .toList();
    }


    private FichierDTO mapFichier(Fichier f) {
        FichierDTO dto = new FichierDTO();
        dto.setId(f.getId());
        dto.setNom(f.getNom());
        dto.setType(f.getType());
        dto.setUrl(f.getUrl());
        dto.setTaille(f.getTaille());
        dto.setFormat(f.getFormat());
        dto.setDescription(f.getDescription());


        if (f.getModule() != null) {
            ModuleDTO moduleDto = new ModuleDTO();
            moduleDto.setId(f.getModule().getId());
            moduleDto.setTitre(f.getModule().getTitre());
            dto.setModule(moduleDto);
        }

        if (f.getFormation() != null) {
            FormationDTO formationDto = new FormationDTO();
            formationDto.setId(f.getFormation().getId());
            formationDto.setTitre(f.getFormation().getTitre());
            dto.setFormation(formationDto);
        }

        return dto;
    }

    private String determineFileType(String extension) {
        String ext = extension.toLowerCase();
        if (ext.endsWith("mp4") || ext.endsWith("avi") || ext.endsWith("mov")) return "VIDEO";
        if (ext.endsWith("jpg") || ext.endsWith("png") || ext.endsWith("jpeg")) return "IMAGE";
        if (ext.endsWith("pdf")) return "PDF";
        return "AUTRE";
    }
}