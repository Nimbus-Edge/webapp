#!/bin/bash

# free up memory, and allocate 1gb swap for mysql server
free -m

sudo fallocate -l 1G /swapfile  # Create a 1GB swap file
sudo chmod 600 /swapfile        # Set the correct permissions
sudo mkswap /swapfile           # Set up the swap file
sudo swapon /swapfile           # Enable the swap file

# scp app zip file from local to host
mkdir cloud

scp -i ~/.ssh/digital-ocean /Users/deepakviswanadha/Cloud_Playground/webapp.zip root@143.244.155.171:/root/cloud

# PART-1
sudo apt update

# install unzip dependency
apt install unzip

# cd to cloud, where zip file is copied to
cd /root/cloud

#unzip the file
unzip webapp.zip

#create resources folder and
cd webapp/src/main

mkdir resources

#copy app properties file into resources from local to host
scp -i ~/.ssh/digital-ocean /Users/deepakviswanadha/Cloud_Playground/Assignment2/application.properties root@143.244.155.171:/root/cloud/webapp/src/main/resources

# cd to root
cd

# install mysql server
sudo apt install -y mysql-server

# start the server, create a db called users, assign admin admin rights to
# host 127.0.01
sudo systemctl start mysql
sudo mysql -e  "CREATE DATABASE IF NOT EXISTS users;"
sudo mysql -e  "CREATE USER 'root'@'127.0.0.1' IDENTIFIED BY '';"
sudo mysql -e  "GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;"
sudo mysql -e  "FLUSH PRIVILEGES;"

# install java jdk
sudo apt install -y openjdk-17-jdk

# cd to app dir
cd /root/cloud/webapp

# build the app
./gradlew build -x test

#create a boot jar
./gradlew bootJar

# run the jar file
java -jar build/libs/webapp-0.0.1-SNAPSHOT.jar

# allow firewall for 8080
sudo ufw allow 8080

# run the test cases
./gradlew test --info

# install lynx to visualize the reports
sudo apt install lynx

# open the reports
lynx build/reports/tests/test/index.html