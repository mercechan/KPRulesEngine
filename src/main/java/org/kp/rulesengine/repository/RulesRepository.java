package org.kp.rulesengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.kp.rulesengine.model.Rules;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


@Repository
public interface RulesRepository extends JpaRepository<Rules, Long> {
    Page<Rules> findByRuleSetId (Long ruleSetId, Pageable pageable);
    Optional<Rules> findByIdAndRuleSetId (Long id, Long ruleSetId);
}
