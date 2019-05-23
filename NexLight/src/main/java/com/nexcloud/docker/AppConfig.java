package com.nexcloud.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Value("${docker.socket}")
	private String dockerSocket;

	@Autowired
	public void init(ApplicationContext context) {
		ModuleService.setContext(context);
	}

	public String getDockerSocket() {
		return dockerSocket;
	}
}
