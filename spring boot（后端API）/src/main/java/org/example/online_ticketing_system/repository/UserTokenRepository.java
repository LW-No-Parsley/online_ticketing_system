package org.example.online_ticketing_system.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.online_ticketing_system.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);
    
    Optional<UserToken> findByUserId(Long userId);
    
    @Query("SELECT t FROM UserToken t WHERE t.userId = :userId AND t.expiresAt > :now")
    List<UserToken> findValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserToken t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
