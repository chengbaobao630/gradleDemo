package com.example;

import com.example.rest.JerseyConfig;
import com.example.schedule.executor.CcScheduledThreadPoolExecutor;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GradleDemo2Application{

	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private String redisPort;

	public static void main(String[] args) {
		SpringApplication.run(GradleDemo2Application.class, args);
	}
	@Bean
	public ServletRegistrationBean jerseyServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
		// our rest resources will be available in the path /rest/*
		registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
		return registration;
	}

	@Bean(name = "scheduledExec")
	public CcScheduledThreadPoolExecutor getScheduledExec(){
		return new CcScheduledThreadPoolExecutor(4);
	}

	@Bean
	public ShardedJedisPool initJedisPool() {
		ShardedJedisPool jedisPool=null;
		List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
		JedisShardInfo jedisShardInfo = new JedisShardInfo(redisHost, redisPort);
		jedisShardInfo.setSoTimeout(1000);
		jedisShardInfos.add(jedisShardInfo);
		jedisPool = new ShardedJedisPool(createJedisConfig(), jedisShardInfos);
		return jedisPool;
	}

	private JedisPoolConfig createJedisConfig() {
		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		jedisConfig.setMaxIdle(300);
		jedisConfig.setMaxTotal(300);
		jedisConfig.setTestOnReturn(false);
		jedisConfig.setTestOnBorrow(false);
		return jedisConfig;
	}


}
