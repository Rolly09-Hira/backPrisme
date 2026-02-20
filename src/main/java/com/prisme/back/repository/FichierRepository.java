package com.prisme.back.repository;

import com.prisme.back.entity.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FichierRepository extends JpaRepository<Fichier, Long> {

    List<Fichier> findByModuleId(Long moduleId);
    List<Fichier> findByFormationId(Long formationId);
    @Query("SELECT f FROM Fichier f WHERE f.module.matiere.id = :matiereId")
    List<Fichier> findByMatiereId(@Param("matiereId") Long matiereId);
}
