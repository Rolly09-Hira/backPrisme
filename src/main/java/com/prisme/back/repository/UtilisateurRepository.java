package com.prisme.back.repository;

import com.prisme.back.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    List<Utilisateur> findByRole(String role);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ADMIN' AND u.actif = true")
    List<Utilisateur> findActiveAdmins();

    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true")
    List<Utilisateur> findAllActive();

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
}