#!/usr/bin/with-contenv ash

for FILENAME in /var/run/s6/container_environment/FILE__*; do
  VAR_VALUE_FILE=$(cat ${FILENAME})
  VAR_NAME="${FILENAME#'/var/run/s6/container_environment/FILE__'}"

  if [ -f "$VAR_VALUE_FILE" ]; then
    cat "$VAR_VALUE_FILE" > "/var/run/s6/container_environment/${VAR_NAME}"
    echo "[env-vars] Variable $VAR_NAME set from ${VAR_VALUE_FILE}"
  else
    echo "[env-vars] Variable $VAR_NAME could not be set from ${VAR_VALUE_FILE}. File not found."
  fi  

done