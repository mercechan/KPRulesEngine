package org.kp.rulesengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Entity
@Table(name = "Rules")
@Data
@EqualsAndHashCode
public class Rules extends AuditModel {

	private static final long serialVersionUID = 8075480913072799924L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Size(max = 255)
    @Column(unique = true, name = "name")
    private String name;    
    
    @Column(nullable = true, name = "rule_cond")
    @Size(max = 1000)
    private String rule_cond;	
	
    @Column(nullable = true, name = "rule_cons")
    @Size(max = 1000)
    private String rule_cons;	
	
    @NotNull
    @Column(name = "salience")
	private Integer salience;
    
    @NotNull
    @Size(max = 255)
    @Column(name = "activationGroup")
    private String activationGroup;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruleSetId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private RuleSets ruleSet;
}
