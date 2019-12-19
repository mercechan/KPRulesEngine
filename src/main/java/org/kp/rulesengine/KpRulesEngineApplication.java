package org.kp.rulesengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "org.kp.rulesengine.repository")
@EntityScan(basePackages = "org.kp.rulesengine.model")
public class KpRulesEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpRulesEngineApplication.class, args);
	}

}
