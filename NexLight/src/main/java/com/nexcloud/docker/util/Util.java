package com.nexcloud.docker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	
	// local test
	public static final String API_VERSION = "v1.29";

	//public static final String URI_CONTAINER_LIST = "/containers/json";
	public static final String URI_CONTAINER_LIST = "/containers/json?all=true";
	public static final String URI_CONTAINER_INSPECT = "/containers/%s/json";
	public static final String URI_CONTAINER_PROCESS = "/containers/%s/top";
	public static final String URI_CONTAINER_LOG = "/containers/%s/logs?stderr=1";
	public static final String URI_CONTAINER_STAT = "/containers/%s/stats?stream=false";
	
	public static final String URI_IMAGES_LIST = "/images/json";
	public static final String URI_IMAGES_INSPECT = "/images/%s/json";
	
	public static final String URI_NETWORKS_LIST = "/networks";
	public static final String URI_NETWORKS_INSPECT = "/networks/%s";
	
	public static final String URI_VOLUMES_LIST = "/volumes";
	public static final String URI_VOLUMES_INSPECT = "/volumes/%s";
	
	public static final String URI_SYSTEM_INFO = "/info";
	public static final String URI_SYSTEM_VERSION = "/version";
}