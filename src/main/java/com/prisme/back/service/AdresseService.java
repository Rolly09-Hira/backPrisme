package com.prisme.back.service;

import com.prisme.back.dto.AdresseRequest;
import com.prisme.back.dto.AdresseResponse;
import com.prisme.back.entity.Adresse;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.repository.AdresseRepository;
import com.prisme.back.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdresseService {

    private final AdresseRepository adresseRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ✅ CREATE (PUBLIC)
    public AdresseResponse createAdresse(AdresseRequest request) {

        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (adresseRepository.findByUtilisateurId(utilisateur.getId()).isPresent()) {
            throw new RuntimeException("Cet utilisateur possède déjà une adresse");
        }

        Adresse adresse = new Adresse();
        adresse.setRue(request.getRue());
        adresse.setVille(request.getVille());
        adresse.setCodePostal(request.getCodePostal());
        adresse.setPays(request.getPays());
        adresse.setComplement(request.getComplement());
        adresse.setUtilisateur(utilisateur);

        Adresse saved = adresseRepository.save(adresse);

        return new AdresseResponse(
                saved.getId(),
                saved.getRue(),
                saved.getVille(),
                saved.getCodePostal(),
                saved.getPays(),
                saved.getComplement(),
                saved.getUtilisateur().getId()
        );
    }

    // 🔐 GET
    public AdresseResponse getAdresseById(Long id) {

        Adresse adresse = adresseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));

        return new AdresseResponse(
                adresse.getId(),
                adresse.getRue(),
                adresse.getVille(),
                adresse.getCodePostal(),
                adresse.getPays(),
                adresse.getComplement(),
                adresse.getUtilisateur().getId()
        );
    }

    // 🔐 UPDATE
    public AdresseResponse updateAdresse(Long id, AdresseRequest request) {

        Adresse adresse = adresseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));

        adresse.setRue(request.getRue());
        adresse.setVille(request.getVille());
        adresse.setCodePostal(request.getCodePostal());
        adresse.setPays(request.getPays());
        adresse.setComplement(request.getComplement());

        Adresse updated = adresseRepository.save(adresse);

        return new AdresseResponse(
                updated.getId(),
                updated.getRue(),
                updated.getVille(),
                updated.getCodePostal(),
                updated.getPays(),
                updated.getComplement(),
                updated.getUtilisateur().getId()
        );
    }

    // 🔐 DELETE
    public void deleteAdresse(Long id) {

        if (!adresseRepository.existsById(id)) {
            throw new RuntimeException("Adresse introuvable");
        }

        adresseRepository.deleteById(id);
    }
}
