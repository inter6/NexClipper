package com.nexcloud.docker.util;

import com.nexcloud.docker.AppConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.nexcloud.docker.util.Util.API_VERSION;

@Component
public class Command {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	@Autowired
	private AppConfig appConfig;

	public String procDockerApi(String uri) {
		String dockerCommand = dockerCommand(uri);
		return new Command().execCommand(dockerCommand);
	}

	public InputStream procDockerApiStream(String uri) throws IOException {
		String dockerCommand = dockerCommand(uri);
		return new Command().execStream(dockerCommand);
	}

	private String dockerCommand(String uri) {
		String dockerCommand = String.format("curl --unix-socket %s http:/%s%s", appConfig.getDockerSocket(), API_VERSION, uri);
		//String docker_command = String.format("%s%s", unix_socket, uri);
		logger.info(dockerCommand);
		return dockerCommand;
	}

	private String execCommand(String cmd) {
		try (InputStream input = execStream(cmd)) {
			List<String> results = IOUtils.readLines(input, StandardCharsets.UTF_8);
			return String.join("\n", results);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private InputStream execStream(String cmd) throws IOException {
		Process process = Runtime.getRuntime().exec(cmd);
		return process.getInputStream();
	}
}
