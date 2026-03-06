# 📦 MinIO Object Storage

## Overview
MinIO is an S3-compatible object storage server. We use it to store physical files.

## Usage
- **Documents**: Driver licenses, insurance certificates, and vehicle registrations.
- **POD**: Proof of Delivery photos/signatures.

## Access
- **API (App usage)**: `http://localhost:9000`
- **Console (Browser)**: [http://localhost:9001](http://localhost:9001)

## Credentials
- **Access Key**: `minioadmin`
- **Secret Key**: `minioadmin`

## AWS CLI Integration
You can use the AWS CLI to interact with it:
```bash
aws --endpoint-url http://localhost:9000 s3 ls
```
