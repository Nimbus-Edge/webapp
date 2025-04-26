variable "app_jar" {
  type    = string
  default = "webapp-0.0.1-SNAPSHOT.jar"
}

source "amazon-ebs" "ubuntu" {
  ami_name      = "custom-springboot-mysql-ami"
  instance_type = "t2.micro"
  region        = "us-east-1"
  source_ami_filter {
    filters = {
      name                = "ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"
      root-device-type    = "ebs"
      virtualization-type = "hvm"
    }
    most_recent = true
    owners      = ["314146298591"] 
  }
  ssh_username = "ubuntu"
}

build {
  sources = ["source.amazon-ebs.ubuntu"]

  provisioner "shell" {
    inline = [
      # Update system and install required packages
      "sudo apt-get update",
      "free -m"
      "sudo fallocate -l 1G /swapfile"  # Create a 1GB swap file
      "sudo chmod 600 /swapfile"        # Set the correct permissions
      "sudo mkswap /swapfile"           # Set up the swap file
      "sudo swapon /swapfile "          # Enable the swap file
      "sudo apt-get install -y openjdk-17-jdk mysql-server",
      
      # Enable and start MySQL service
      "sudo systemctl enable mysql",
      "sudo systemctl start mysql",
      
      # MySQL setup: Create 'users' database and configure root user
      "sudo mysql -e \"CREATE DATABASE IF NOT EXISTS users;\"",
      "sudo mysql -e \"CREATE USER 'root'@'127.0.0.1' IDENTIFIED BY '';\"",
      "sudo mysql -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;\"",
      "sudo mysql -e \"FLUSH PRIVILEGES;\"",

      # Create a non-login user 'csye6225' with primary group 'csye6225'
      "sudo groupadd csye6225",
      "sudo useradd -r -s /usr/sbin/nologin -g csye6225 csye6225",
      
      # Create application directory and copy the JAR file
      "sudo mkdir -p /opt/myapp",
      "sudo cp ${var.app_jar} /opt/myapp/myapp.jar",
      
      # Change ownership of the application artifacts
      "sudo chown -R csye6225:csye6225 /opt/myapp",
      
      # Start the application as 'csye6225' user
      "sudo -u csye6225 java -jar /opt/myapp/myapp.jar &"
    ]
  }

  post-processor "amazon-ami-management" {
    keep_releases = 3
  }
}
