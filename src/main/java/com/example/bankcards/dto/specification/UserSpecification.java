package com.example.bankcards.dto.specification;

import com.example.bankcards.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for users filter
 */
@Slf4j
public class UserSpecification {
    public static Specification<User> filter(String name) {
        log.info("Filtering User by {}", name);
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("login")),
                        "%" + name.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
