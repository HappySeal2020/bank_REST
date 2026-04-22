package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Card
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    /**
     * retrieves the list of cards for the administrator to change their status
     * @return List <Card>
     */
    @Query("SELECT e FROM Card e WHERE e.currentStatus <> e.requestedStatus ")
    List<Card> findMismatched();

    /**
     * retrieves the list of cards for the user
     * @param login user
     * @return List <Card>
     */
    @Query("SELECT e FROM Card e WHERE e.user.login = :login")
    List<Card> findOwnCards(@Param("login") String login);

    /**
     * retrieves the Cards Balance for the user
     * @param login user
     * @return BigDecimal balance
     */
    @Query ("SELECT SUM (c.balance) FROM Card c WHERE c.user.login = :login")
    BigDecimal getTotalBalance(@Param("login") String login);

    /**
     * retrieves user card by login and page
     * @param login user
     * @param pageable pageable
     * @return Page<Card>
     */
    Page<Card> findByUserLogin(String login, Pageable pageable);

    /**
     * find card by id and login
     * @param id card's id
     * @param login login
     * @return Optional<Card>
     */
    Optional<Card> findByIdAndUserLogin(Long id, String login);
}
