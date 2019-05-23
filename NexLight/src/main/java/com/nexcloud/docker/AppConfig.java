package com.nexcloud.docker;

import com.nexcloud.docker.util.Command;
import com.nexcloud.docker.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static com.nexcloud.docker.util.Util.API_VERSION;

@Configuration
public class AppConfig {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	@Value("${docker.socket}")
	private String dockerSocket;

	@Autowired
	public void init(ApplicationContext context) {
		ModuleService.setContext(context);
	}

	public String procDockerApi(String uri) {
		Command cmd = new Command();
		String dockerCommand = String.format("curl --unix-socket %s http:/%s%s", dockerSocket, API_VERSION, uri);
		//String docker_command = String.format("%s%s", unix_socket, uri);
		logger.info(dockerCommand);

		return cmd.execCommand(dockerCommand);
	}
}
