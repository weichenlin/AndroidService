package cc.aznc.demo.remotetimeservicehost;

import cc.aznc.demo.remotetimeservicehost.TimeObserver;

interface TimeService {
	void registObserver(TimeObserver o);
}