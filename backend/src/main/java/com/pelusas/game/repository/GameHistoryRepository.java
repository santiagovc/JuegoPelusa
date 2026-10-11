package com.pelusas.game.repository;

import com.pelusas.game.entity.GameHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para el histórico de partidas.
 */
@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistoryEntity, String> {
    
    List<GameHistoryEntity> findAllByOrderByPlayedAtDesc();
}
