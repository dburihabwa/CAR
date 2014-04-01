#! /bin/sh

xterm -T "REGISTRY" -e "java -jar RMI-Registry-0.0.1-SNAPSHOT.jar" &
sleep 1
for file in $(find -maxdepth 1 -type f -iname "*.properties"); do
   xterm -e "java -jar RMI-Node-0.0.1-SNAPSHOT.jar $file" &
done
sleep 3
xterm -T "COMMANDER" -hold -e "java -jar RMI-Commander-0.0.1-SNAPSHOT.jar localhost 1099 un" &
