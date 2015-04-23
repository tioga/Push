@echo off
del \\parwinproto\d$\proto\tioga-push-server-grizzly\lib\*.jar
copy C:\dvlp\3rd-party\tioga-solutions\Push\tioga-push-server-grizzly\build\install\tioga-push-server-grizzly\lib\*.jar \\parwinproto\d$\proto\tioga-push-server-grizzly\lib
copy C:\dvlp\3rd-party\tioga-solutions\Push\tioga-push-server-grizzly\build\install\tioga-push-server-grizzly\server-start.bat \\parwinproto\d$\proto\tioga-push-server-grizzly
