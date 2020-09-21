import os
import platform

if platform.system()=='Windows':
	cmd = 'sh src/bin/shutdown.bat'
else:
	cmd = 'sh src/bin/shutdown.sh'
os.system(cmd)