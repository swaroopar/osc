terraform {
  required_providers {
    huaweicloud = {
      source = "huaweicloud/huaweicloud"
      version = "~> 1.51.0"
    }
  }
}

provider "huaweicloud" {
  region = var.region
}
