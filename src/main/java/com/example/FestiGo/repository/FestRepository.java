package com.example.FestiGo.repository;

import com.example.FestiGo.domain.Fest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FestRepository extends JpaRepository<Fest, UUID> {

    // Find upcoming fests (date >= today) ordered by date
    @Query("SELECT f FROM Fest f WHERE f.date >= :today ORDER BY f.date ASC")
    List<Fest> findUpcomingFests(@Param("today") LocalDate today);

    // Find upcoming fests in a specific state
    @Query("SELECT f FROM Fest f WHERE f.date >= :today AND LOWER(f.state) = LOWER(:state) ORDER BY f.date ASC")
    List<Fest> findUpcomingFestsByState(@Param("today") LocalDate today, @Param("state") String state);

    // Find upcoming fests by genre (for interest-based recommendations)
    @Query("SELECT f FROM Fest f WHERE f.date >= :today AND LOWER(f.genre) IN :genres ORDER BY f.date ASC")
    List<Fest> findUpcomingFestsByGenres(@Param("today") LocalDate today, @Param("genres") List<String> genres);

    // Get overall best upcoming fests (ordered by attendee count and date
    // proximity)
    @Query(value = """
            SELECT f.* FROM fests f
            LEFT JOIN (
                SELECT fest_id, COUNT(*) as cnt FROM fest_attendance WHERE attended = true GROUP BY fest_id
            ) fa ON f.id = fa.fest_id
            WHERE f.date >= :today
            ORDER BY COALESCE(fa.cnt, 0) DESC, f.date ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Fest> findPopularUpcomingFests(@Param("today") LocalDate today, @Param("limit") int limit);

    // Personalized recommendations based on user's past genres and location
    @Query(value = """
            WITH user_genres AS (
                SELECT DISTINCT LOWER(f.genre) as genre
                FROM fest_attendance fa
                JOIN fests f ON fa.fest_id = f.id
                WHERE fa.user_id = :userId AND fa.attended = true AND f.genre IS NOT NULL
            ),
            user_location AS (
                SELECT city, state FROM users WHERE id = :userId
            ),
            scored_fests AS (
                SELECT f.*,
                    CASE WHEN LOWER(f.genre) IN (SELECT genre FROM user_genres) THEN 2 ELSE 0 END +
                    CASE WHEN LOWER(f.state) = LOWER((SELECT state FROM user_location)) THEN 1.5 ELSE 0 END +
                    CASE WHEN LOWER(f.city) = LOWER((SELECT city FROM user_location)) THEN 1 ELSE 0 END +
                    (1.0 / (1 + EXTRACT(DAY FROM (f.date - CURRENT_DATE)))) as score
                FROM fests f
                WHERE f.date >= :today
                AND f.id NOT IN (
                    SELECT fest_id FROM fest_attendance WHERE user_id = :userId AND attended = true
                )
            )
            SELECT * FROM scored_fests ORDER BY score DESC, date ASC LIMIT :limit
            """, nativeQuery = true)
    List<Fest> findPersonalizedRecommendations(@Param("userId") UUID userId, @Param("today") LocalDate today,
            @Param("limit") int limit);
}
