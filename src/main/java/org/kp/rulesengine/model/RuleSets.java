package org.kp.rulesengine.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "RuleSets")
public class RuleSets extends AuditModel {

	private static final long serialVersionUID = -8179724845359681502L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @NotNull
    @Size(max = 255)
    @Column(unique = true, name = "name")
    private String name;	
	
    @NotNull
    @Size(max = 15000)
    @Column(name = "content")
    private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
}
