package com.nameless;

import java.lang.management.ManagementFactory;

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
}
