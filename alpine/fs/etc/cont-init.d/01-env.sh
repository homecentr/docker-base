#!/usr/bin/with-contenv ash

MATCHING_FILES=$(find /var/run/s6/container_environment/ -name "FILE__*" | wc -l)

if [ $MATCHING_FILES -le 0 ]; then
  exit 0
fi

for FULL_FILENAME in /var/run/s6/container_environment/FILE__*; do
  FILENAME="${FULL_FILENAME#'/var/run/s6/container_environment/'}"

  #if [[ $FILENAME = "FILE__"* ]]; then
    VAR_VALUE_FILE=$(cat ${FULL_FILENAME})
    VAR_NAME="${FILENAME#'FILE__'}"

    if [ -f "$VAR_VALUE_FILE" ]; then
      cat "$VAR_VALUE_FILE" > "/var/run/s6/container_environment/${VAR_NAME}"
      echo "[env-vars] Variable $VAR_NAME set from ${VAR_VALUE_FILE}"
    else
      echo "[env-vars] Variable $VAR_NAME could not be set from ${VAR_VALUE_FILE}. File not found."
    fi
done