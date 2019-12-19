package org.kp.rulesengine.repository;

import org.kp.rulesengine.model.RuleSets;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RuleSetsRepository extends JpaRepository<RuleSets, Long> {

}
