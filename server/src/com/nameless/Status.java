package com.nameless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;

public class Status {
	com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean)
			ManagementFactory.getOperatingSystemMXBean();
	long total;
	long free;
	double percentsRAMUsage;
	double t;
	double f;
	double result;
	String info;

	public String statusServer() {
		total = mxbean.getTotalPhysicalMemorySize();
		free = mxbean.getFreePhysicalMemorySize();
		percentsRAMUsage = (double) (total - free) / total;
		t = (double) Math.round((mxbean.getTotalPhysicalMemorySize() * 1.0 /1024/1024/1024) * 100) / 100;
		f = (double) Math.round(((mxbean.getTotalPhysicalMemorySize() - mxbean.getFreePhysicalMemorySize())
				* 1.0/1024/1024/1024) * 100) / 100;
		result = Math.round(percentsRAMUsage * 100.0);
		info = f + " GB / " + t + " GB (" + result + "%)";
		return info;
	}

	public String getIP() throws IOException {
		URL url = new URL("https://api.ipify.org");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String ip = bufferedReader.readLine();
		return ip;
	}

}
