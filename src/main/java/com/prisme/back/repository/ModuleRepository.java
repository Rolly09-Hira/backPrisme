package com.prisme.back.repository;

import com.prisme.back.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByMatiereId(Long matiereId);
}
