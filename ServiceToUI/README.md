# Android Example - Update UI by service

### Overview

The main purpose here is to practice communication between Service and Activity.

I will use a service to update current system time to UI.

to identify which service we are using, service will generate a random id when created.
so you can check the id to know which service instance the app are using

* [LocalTimeService](https://github.com/weichenlin/AndroidService/tree/master/ServiceToUI/LocalTimeService) will using local service and let Activity implement a callback interface, service will call it for update current time.
* [RemoteTimeServiceHost](https://github.com/weichenlin/AndroidService/tree/master/ServiceToUI/RemoteTimeServiceHost) let service running on another process, and using AIDL for communication
* [RemoteTimeServiceGuest](https://github.com/weichenlin/AndroidService/tree/master/ServiceToUI/RemoteTimeServiceGuest) have no service, it will use the service from RemoteTimeServiceHost by the same AIDL interface, so you will need install RemoteTimeServiceHost before running this guest app

All the 3 app have only one Activity, that display service id and current time.

### Sub subjects

you may found something useful in those example as well:

* using AIDL
* to start a service that owned by other app
* use permission to limit access to a exported service
* to check what process id the app are using:
```bash
$ adb shell top -n 1 | grep timeservice
28644  0   0% S    12 665608K  27936K  fg u0_a107  cc.aznc.demo.remotetimeserviceguest
28386  1   0% S    11 658316K  21628K  fg u0_a105  cc.aznc.demo.remotetimeservicehost.RemoteTimeService
28283  0   0% S    11 658308K  21428K  bg u0_a100  cc.aznc.demo.localtimeservice
28623  0   0% S    12 665616K  28172K  bg u0_a105  cc.aznc.demo.remotetimeservicehost
```
