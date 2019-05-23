package com.nexcloud.docker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nexcloud.docker.container.list.domain.Container;
import com.nexcloud.docker.container.log.domain.ContainerLog;
import com.nexcloud.docker.container.stats.domain.History;
import com.nexcloud.docker.container.stats.domain.Resource;
import com.nexcloud.docker.resource.ResourceLoader;
import com.nexcloud.docker.util.Command;
import com.nexcloud.docker.util.Util;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class ContainerService {
	private static final Logger logger = LoggerFactory.getLogger(ContainerService.class);
	private static final int log_cnt = 10000;

	@Autowired
	private Command command;

	public String getContainerInspect(String id) {
		String uri = String.format(Util.URI_CONTAINER_INSPECT, id);
		return command.procDockerApi(uri);
	}

	public String getContainerProcess(String id) {
		String uri = String.format(Util.URI_CONTAINER_PROCESS, id);
		return command.procDockerApi(uri);
	}

	public String getContainerLog(String container_id) throws IOException {
		String uri = String.format(Util.URI_CONTAINER_LOG, container_id);
		long since = DateTime.now().minusDays(1).getMillis() / 1000;
		String sinceUri = String.format("%s&since=%d", uri, since);

		try (InputStream input = command.procDockerApiStream(sinceUri)) {
			List<ContainerLog> logs = parseLog(input);
			if (logs.size() > log_cnt) {
				logs = logs.subList(logs.size() - log_cnt, logs.size());
			}
			return new Gson().toJson(logs);
		}
	}

	public ModelMap getContainerLogHistory(String container_id, String time) throws IOException {
		String uri = String.format(Util.URI_CONTAINER_LOG, container_id);
		long since = Long.parseLong(time);
		String sinceUri = String.format("%s&since=%d", uri, since);

		try (InputStream input = command.procDockerApiStream(sinceUri)) {
			List<ContainerLog> logs = parseLog(input);

			ModelMap model = new ModelMap();
			model.put("result", "SUCCESS");
			model.put("log", logs);
			return model;
		}
	}

	private List<ContainerLog> parseLog(InputStream logStream) throws IOException {
		List<ContainerLog> logs = new ArrayList<>();

		while (true) {
			byte[] header = new byte[8];
			int todo = 8;
			while (todo > 0) {
				int i = logStream.read(header, 8 - todo, todo);
				if (i < 0) {
					return logs;
				}
				todo -= i;
			}

			int size = ((header[4] & 0xff) << 24) + ((header[5] & 0xff) << 16) + ((header[6] & 0xff) << 8) + (header[7] & 0xff);

			String stream;
			switch (header[0]) {
				case 1:
					stream = "stdout";
					break;
				case 2: // STDERR
					stream = "stderr";
					break;
				default:
					throw new IOException("Unexpected application/vnd.docker.raw-stream frame type " + Arrays.toString(header));
			}

			byte[] payload = new byte[size];
			int received = 0;
			while (received < size) {
				int i = logStream.read(payload, received, size - received);
				if (i < 0) {
					return logs;
				}
				received += i;
			}

			Long time = null;
			String log = new String(payload, 0, received, StandardCharsets.UTF_8);
			if (log.length() >= 30) {
				String timeStr = log.substring(0, 30);
				try {
					time = ISODateTimeFormat.dateTime().parseMillis(timeStr) / 1000;
					log = log.substring(30);
				} catch (Throwable e) {
					logger.warn("time parse fail: {}", timeStr);
				}
			}

			logs.add(new ContainerLog(time, stream, log));
		}
	}

	public ModelMap getStatHistory(String container_id, String time ) {
		ModelMap model = new ModelMap();
		History history = (History)ResourceLoader.getInstance().getResource("history_"+container_id);

		List<Resource> resources  = new ArrayList<Resource>();

		if( time == null ) {
			for( Resource  resource : history.getUseds() )
				resources.add(resource);
		}
		else {
			if (history != null) {
				for (Resource resource : history.getUseds()) {
					if (resource.getTimestamp() > Long.parseLong(time)) {
						resources.add(resource);
					}
				}
			}
		}

		model.put("result", "SUCCESS");
		model.put("list", resources);

		return model;
	}

	public String getContainerList(HttpServletRequest request) {
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		List<Container> result = new ArrayList<Container>();
		List<Container> containers = (ArrayList<Container>)ResourceLoader.getInstance().getResource("con_list");
		Iterator iterator = containers.iterator();

		if (request.getParameter("name").equals("")
				&& request.getParameter("status").equals("")
				&& request.getParameter("searchTxt").equals("")) {

			while (iterator.hasNext()) {
				Container con = gson.fromJson((String)iterator.next(), Container.class);
				result.add(con);
			}

			return gson.toJson(result);
		}

        while (iterator.hasNext()) {
    		Container con = gson.fromJson((String)iterator.next(), Container.class);

    		if(!request.getParameter("name").equals("") && con.getNames().toString().contains(request.getParameter("name"))) {
    			result.add(con);
			}

    		if (con.getLabels().getMESOS_TASK_ID() != null) {
	    		if(!request.getParameter("name").equals("") && con.getLabels().getMESOS_TASK_ID().contains(request.getParameter("name"))) {
	    			result.add(con);
				}
    		}

    		if(!request.getParameter("status").equals("") && con.getState().equals(request.getParameter("status"))) {
    			result.add(con);
			}

    		else if(!request.getParameter("searchTxt").equals("") && con.getId().contains(request.getParameter("searchTxt"))) {
				result.add(con);
			}

    		else if(!request.getParameter("searchTxt").equals("") && con.getNames().toString().contains(request.getParameter("searchTxt"))) {
				result.add(con);
			}
        }

        return gson.toJson(result);
	}
}