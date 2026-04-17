#!/usr/bin/env bash
# LocalStack ready.d hook — create default dev bucket (matches post-service default AWS_S3_BUCKET).
set -euo pipefail
if command -v awslocal >/dev/null 2>&1; then
  awslocal s3 mb "s3://instagram-post-media" || true
else
  echo "01-create-s3-bucket: awslocal not in PATH; post-service S3BucketInitializer will create the bucket." >&2
fi
