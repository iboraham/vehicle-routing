import os
import time

cmd = 'sh src/bin/startup.sh'
os.system(cmd)
time.sleep(2)
os.system("open -a 'Google Chrome' http://localhost:8080/senior_project")
