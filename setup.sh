#!/bin/bash

echo 'Update dependencies:' 
sudo apt-get update

echo 'Current Directory:' && pwd
echo 'Files in Home Directory:' && ls -l /home/ubuntu/

echo 'Creating Application Directory'
sudo mkdir -p /opt/myapp || { echo 'Failed to create /opt/myapp'; exit 1; }

echo 'Creating user csye6225'
sudo groupadd csye6225 || { echo 'Failed to create group csye6225'; exit 1; }
sudo useradd -r -s /usr/sbin/nologin -g csye6225 csye6225 || { echo 'Failed to create user csye6225'; exit 1; }

echo 'Moving JAR file'
if [ -f /home/ubuntu/myapp.jar ]; then
    sudo mv /home/ubuntu/myapp.jar /opt/myapp/myapp.jar
else
    echo 'JAR file not found'; exit 1
fi

echo 'Changing ownership fo the jar file'
sudo chown csye6225:csye6225 /opt/myapp/myapp.jar || { echo 'Failed to change ownership of JAR file'; exit 1; }
sudo chmod -R 755 /opt/myapp

echo 'Installing Java'
if ! java -version &>/dev/null; then
    sudo apt-get install -y openjdk-17-jdk || { echo 'Failed to install Java'; exit 1; }
fi

echo 'Moving the systemd service file'
if [ -f /home/ubuntu/myapp.service ]; then
    sudo mv /home/ubuntu/myapp.service /etc/systemd/system/myapp.service
    echo 'Service file moved successfully'
else
    echo 'Service file not found'; exit 1
fi

echo 'Changing ownership fo the service'
sudo chown csye6225:csye6225 /etc/systemd/system/myapp.service || { echo 'Failed to change ownership of service file'; exit 1; }

echo 'Install curl'
sudo apt-get install -y curl

echo 'Install CloudWatch Agent'
curl https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb -o amazon-cloudwatch-agent.deb
sudo dpkg -i amazon-cloudwatch-agent.deb
rm amazon-cloudwatch-agent.deb


echo "Creating CloudWatch Agent configuration file"
cat <<EOF | sudo tee /opt/myapp/cloudwatch-config.json
{
    "logs": {
        "logs_collected": {
            "files": {
                "collect_list": [
                    {
                        "file_path": "/opt/myapp/myapp.log",
                        "log_group_name": "myapp-log-group",
                        "log_stream_name": "{instance_id}",
                        "timezone": "UTC"
                    }
                ]
            }
        }
    }
}
EOF

echo 'Moving config file of agent'
sudo mv /opt/myapp/cloudwatch-config.json /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

echo 'Reloading systemd'
sudo systemctl daemon-reload

echo 'Setup complete'