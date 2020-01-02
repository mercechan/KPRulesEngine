package org.kp.rulesengine.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Component
public class CharactersQuery implements GraphQLQueryResolver{

	public List<Character> findAllCharacters()
	{
		List<Character> chas = new ArrayList<>();
		Character c1 = new Character();
		c1.setId(1234L);
		c1.setName("John Doe");
		c1.setSsn("7761-2122");
		chas.add(c1);
		
		Character c2 = new Character();
		c2.setId(4321L);
		c2.setName("Jane Doe");
		c2.setSsn("1221-8788");
		chas.add(c2);
		
		return chas;
	}
}
