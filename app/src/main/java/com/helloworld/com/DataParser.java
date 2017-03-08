package com.helloworld.com;

public class DataParser {
	
	public String getAsciStartOfHeading() {
		return "\001";
	}

	public String getAsciUnitSeparator() {
		return "\037";
	}

	public String getAsciRecordSeparator() {
		return "\030";
	}

	public String getAsciEndOfTransmission() {
		return "\004";
	}

	public String getName(String name) {
		return setName(name);
	}

	public String setName(String name) {
		return getAsciStartOfHeading() + name + getAsciUnitSeparator();
	}

	public String getAsciInfoStream() {
		return "\002" + "InfoStream" + "\003";
	}

	public String getAsciNetworkInfoStream() {
		return "\002" + "NetworkInfoStream" + "\003";
	}

	public String getAsciRunningTaskInfoStream() {
		return "\002" + "RunningTaskInfoStream" + "\003";
	}

	public String getAsciSensorInfoStream() {
		return "\002" + "SensorInfoStream" + "\003";
	}

	public String getAsciTimeInfoStream() {
		return "\002" + "TimeInfoStream" + "\003";
	}

	public String getAsciRunningAppProcessInfo() {
		return "\002" + "RunningAppProcessInfo" + "\003";
	}

	public String getAsciRunningServiceInfo() {
		return "\002" + "RunningServiceInfo" + "\003";
	}

	public String getAsciBatteryInfo() {
		return "\002" + "BatteryInfo" + "\003";
	}

	public String getAsciNetworkInfo() {
		return "\002" + "NetworkInfo" + "\003";
	}

	public String getAsciActiveNetworkInfo() {
		return "\002" + "ActiveNetworkInfo" + "\003";
	}

	public String getAsciAllNetworkInfo() {
		return "\002" + "AllNetworkInfo" + "\003";
	}
	
	public String getJNIInfo() {
		return "\002" + "JNIInfo" + "\003";
	}
	
	public String getAsciProcMemo() {
		return "\002" + "ProcMemo" + "\003";
	}
	
	public String getAsciCpuMemo() {
		return "\002" + "CpuMemo" + "\003";
	}
}
