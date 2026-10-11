package com.pelusas.game.repository;

import com.pelusas.game.entity.ActiveGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para partidas activas.
 */
@Repository
public interface ActiveGameRepository extends JpaRepository<ActiveGameEntity, String> {
}
