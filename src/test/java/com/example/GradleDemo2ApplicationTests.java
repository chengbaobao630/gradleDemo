package com.example;

import com.example.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GradleDemo2ApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;

	@Test
	public void contextLoads() {
		Query query=new Query();
		query.addCriteria(Criteria.where("password").is("BB"));
		List<User> userList=mongoTemplate.find(query,User.class);
		assert  userList != null;
	}

}
