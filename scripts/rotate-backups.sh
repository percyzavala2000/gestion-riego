#!/usr/bin/env bash
set -euo pipefail
DIR="${BACKUP_DIR:-backups}"; DAYS="${BACKUP_RETENTION_DAYS:-30}"
find "$DIR" -type f \( -name '*.dump' -o -name '*.dump.sha256' \) -mtime "+$DAYS" -print -delete
