package com.prisme.back.service;

import com.prisme.back.dto.FormationDTO;
import com.prisme.back.dto.MatiereDTO;
import com.prisme.back.dto.ModuleDTO;
import com.prisme.back.entity.Formation;
import com.prisme.back.entity.Matiere;
import com.prisme.back.entity.Module;
import com.prisme.back.repository.MatiereRepository;
import com.prisme.back.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final MatiereRepository matiereRepository;

    public ModuleDTO createModule(ModuleDTO dto) {
        Module m = new Module();
        m.setTitre(dto.getTitre());
        m.setDescription(dto.getDescription());
        m.setDureeEstimee(dto.getDureeEstimee());
        m.setOrdre(dto.getOrdre());

        Matiere matiere = matiereRepository.findById(dto.getMatiereId())
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        m.setMatiere(matiere);

        Module saved = moduleRepository.save(m);
        return mapModule(saved);
    }

    public ModuleDTO getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module non trouvé avec l'id : " + id));
        return mapModule(module);
    }

    public ModuleDTO updateModule(Long id, ModuleDTO dto) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module non trouvé avec l'id : " + id));

        if (dto.getTitre() != null) {
            module.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            module.setDescription(dto.getDescription());
        }
        if (dto.getDureeEstimee() != null) {
            module.setDureeEstimee(dto.getDureeEstimee());
        }
        if (dto.getOrdre() != null) {
            module.setOrdre(dto.getOrdre());
        }
        if (dto.getMatiereId() != null) {
            Matiere matiere = matiereRepository.findById(dto.getMatiereId())
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
            module.setMatiere(matiere);
        }

        Module updated = moduleRepository.save(module);
        return mapModule(updated);
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    // Lister tous les modules
    public List<ModuleDTO> getAllModules() {
        return moduleRepository.findAll()
                .stream()
                .map(this::mapModule)
                .toList();
    }

    // Lister les modules d'une matière donnée
    public List<ModuleDTO> getModulesByMatiere(Long matiereId) {
        return moduleRepository.findByMatiereId(matiereId)   // ← tu dois avoir cette méthode dans ModuleRepository
                .stream()
                .map(this::mapModule)
                .toList();
    }

    private ModuleDTO mapModule(Module m) {
        ModuleDTO dto = new ModuleDTO();
        dto.setId(m.getId());
        dto.setTitre(m.getTitre());
        dto.setDescription(m.getDescription());
        dto.setDureeEstimee(m.getDureeEstimee());
        dto.setOrdre(m.getOrdre());
        dto.setMatiereId(m.getMatiere() != null ? m.getMatiere().getId() : null);

        // Inclusion de la matière (et via elle, la formation)
        if (m.getMatiere() != null) {
            Matiere mat = m.getMatiere();
            MatiereDTO matiereDto = new MatiereDTO();
            matiereDto.setId(mat.getId());
            matiereDto.setNom(mat.getNom());
            matiereDto.setOrdre(mat.getOrdre());

            // Inclusion de la formation dans la matière
            if (mat.getFormation() != null) {
                Formation f = mat.getFormation();
                FormationDTO formationDto = new FormationDTO();
                formationDto.setId(f.getId());
                formationDto.setTitre(f.getTitre());
                formationDto.setCategorie(f.getCategorie());

                matiereDto.setFormation(formationDto);
            }

            dto.setMatiere(matiereDto);
        }

        return dto;
    }
}