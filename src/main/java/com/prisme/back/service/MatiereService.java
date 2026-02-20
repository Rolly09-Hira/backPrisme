package com.prisme.back.service;

import com.prisme.back.dto.FormationDTO;
import com.prisme.back.dto.MatiereDTO;
import com.prisme.back.entity.Formation;
import com.prisme.back.entity.Matiere;
import com.prisme.back.repository.FormationRepository;
import com.prisme.back.repository.MatiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final FormationRepository formationRepository;

    public MatiereDTO createMatiere(MatiereDTO dto) {
        Matiere m = new Matiere();
        m.setNom(dto.getNom());
        m.setDescription(dto.getDescription());
        m.setOrdre(dto.getOrdre());

        Formation formation = formationRepository.findById(dto.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
        m.setFormation(formation);

        Matiere saved = matiereRepository.save(m);
        return mapMatiere(saved);
    }

    public List<MatiereDTO> getMatieresByFormation(Long formationId) {
        return matiereRepository.findByFormationId(formationId)
                .stream()
                .map(this::mapMatiere)
                .toList();
    }

    public MatiereDTO getMatiereById(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id : " + id));
        return mapMatiere(matiere);
    }

    public List<MatiereDTO> getAllMatieres() {
        return matiereRepository.findAll()
                .stream()
                .map(this::mapMatiere)
                .toList();
    }

    public MatiereDTO updateMatiere(Long id, MatiereDTO dto) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id : " + id));

        if (dto.getNom() != null) {
            matiere.setNom(dto.getNom());
        }
        if (dto.getDescription() != null) {
            matiere.setDescription(dto.getDescription());
        }
        if (dto.getOrdre() != null) {
            matiere.setOrdre(dto.getOrdre());
        }
        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId())
                    .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
            matiere.setFormation(formation);
        }

        Matiere updated = matiereRepository.save(matiere);
        return mapMatiere(updated);
    }

    public void deleteMatiere(Long id) {
        matiereRepository.deleteById(id);
    }

    private MatiereDTO mapMatiere(Matiere m) {
        MatiereDTO dto = new MatiereDTO();
        dto.setId(m.getId());
        dto.setNom(m.getNom());
        dto.setDescription(m.getDescription());
        dto.setOrdre(m.getOrdre());

        // On garde formationId pour les mises à jour
        dto.setFormationId(m.getFormation() != null ? m.getFormation().getId() : null);

        // On inclut maintenant les infos de la formation
        if (m.getFormation() != null) {
            Formation f = m.getFormation();
            FormationDTO formationDto = new FormationDTO();  // ou FormationDTO
            formationDto.setId(f.getId());
            formationDto.setTitre(f.getTitre());
            formationDto.setCategorie(f.getCategorie());
            formationDto.setPrix(f.getPrix());
            // ajoute d'autres champs si besoin
            dto.setFormation(formationDto);
        }

        return dto;
    }
}