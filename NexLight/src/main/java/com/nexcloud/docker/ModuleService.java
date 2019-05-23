package com.nexcloud.docker;

import org.springframework.context.ApplicationContext;

public class ModuleService {

	private static ApplicationContext context;

	public static void setContext(ApplicationContext context) {
		ModuleService.context = context;
	}

	public static <T> T getBean(Class<T> type) {
		return context.getBean(type);
	}
}
