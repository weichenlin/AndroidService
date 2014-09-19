package cc.aznc.demo.remotetimeservicehost;

interface TimeObserver {
	void onTimeUpdate(String serviceId, String time);
}