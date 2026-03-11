package com.prisme.back.repository;

import com.prisme.back.entity.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdresseRepository extends JpaRepository<Adresse, Long> {

    Optional<Adresse> findByUtilisateurId(Long utilisateurId);

}
