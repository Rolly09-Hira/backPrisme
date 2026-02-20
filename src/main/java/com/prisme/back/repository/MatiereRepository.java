package com.prisme.back.repository;

import com.prisme.back.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findByFormationId(Long formationId);
}
