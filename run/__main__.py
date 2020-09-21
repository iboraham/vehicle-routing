import os
import time
import platform

if platform.system()=='Windows':
	cmd = 'sh src/bin/startup.bat'
else:
	cmd = 'sh src/bin/startup.sh'
os.system(cmd)
time.sleep(2)
os.system("open -a 'Google Chrome' http://localhost:8080/senior_project")
