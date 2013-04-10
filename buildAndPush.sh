#!/bin/bash

gradle clean war
#set -m # Enable Job Control

scp -i ~/ceswp/roddi.pem apps/build/libs/apps.war root@142.244.197.58:/home/roddi/cssdp/deploy
scp -i ~/ceswp/roddi.pem auth/build/libs/auth.war root@142.244.197.58:/home/roddi/cssdp/deploy 
scp -i ~/ceswp/roddi.pem cache/build/libs/cache.war root@142.244.197.58:/home/roddi/cssdp/deploy
scp -i ~/ceswp/roddi.pem catalogue/build/libs/catalogue.war root@142.244.197.58:/home/roddi/cssdp/deploy 
scp -i ~/ceswp/roddi.pem vfs/build/libs/vfs.war root@142.244.197.58:/home/roddi/cssdp/deploy 

tar czvf database.tar.gz database/ 
scp -i ~/ceswp/roddi.pem database.tar.gz root@142.244.197.58:/home/roddi/cssdp/deploy

# Wait for all parallel jobs to finish
# while [ 1 ]; do fg 2> /dev/null; [ $? == 1 ] && break; done

