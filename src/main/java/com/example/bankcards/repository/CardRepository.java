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

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @Query("SELECT e FROM Card e WHERE e.currentStatus <> e.requestedStatus ")
    List<Card> findMismatched();

    @Query("SELECT e FROM Card e WHERE e.user.login = :login")
    List<Card> findOwnCards(@Param("login") String login);

    @Query ("SELECT SUM (c.balance) FROM Card c WHERE c.user.login = :login")
    BigDecimal getTotalBalance(@Param("login") String login);

    Page<Card> findByUserLogin(String login, Pageable pageable);

}
