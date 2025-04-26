packer {
  required_plugins {
    amazon = {
      version = ">= 1.3.3"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "app_jar" {
  type = string
}

variable "ami_name" {
  type = string
}

variable "instance_type" {
  type = string
}

variable "region" {
  type = string
}

variable "source_ami_name" {
  type = string
}

variable "script_source" {
  type = string
}

variable "service_source" {
  type = string
}

source "amazon-ebs" "ubuntu" {
  ami_name      = var.ami_name
  instance_type = var.instance_type
  region        = var.region
  ssh_username  = "ubuntu"
  source_ami    = var.source_ami_name
}

build {
  sources = ["source.amazon-ebs.ubuntu"]

  # Copy the JAR file
  provisioner "file" {
    source      = var.app_jar
    destination = "/home/ubuntu/myapp.jar"
  }

  # Copy the setup script
  provisioner "file" {
    source      = "setup.sh"
    destination = "/home/ubuntu/setup.sh"
  }

  # Copy the systemd service file to a temporary location
  provisioner "file" {
    source      = "myapp.service"
    destination = "/home/ubuntu/myapp.service"
  }

  provisioner "shell" {
    inline = [
      "chmod +x /home/ubuntu/setup.sh",
      "/home/ubuntu/setup.sh",
    ]
  }

  post-processor "manifest" {
    output = "manifest.json"
  }
}

