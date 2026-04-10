package com.example.bankcards.dto.specification;

import com.example.bankcards.entity.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.ArrayList;

@Slf4j
public class CardSpecification {
public static Specification<Card> filter (String cardNum,
                                          String owner,
                                          String login){
    log.info("Filtering card by card number={}, owner={}, login={} ", cardNum, owner, login);

    return (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        query.distinct(true);
        if (cardNum != null) {
            predicates.add(cb.like(cb.lower(root.get("cardNum4")),
                    "%" + cardNum.toLowerCase() + "%"));
        }

        if (owner != null) {
            predicates.add(cb.like(cb.lower(root.get("owner")),
                    "%" + owner.toLowerCase() + "%"));
        }

        if (login != null) {
            predicates.add(cb.like(cb.lower(root.get("user").get("login")),
                    "%" + login.toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}

}
