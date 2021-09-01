#!/usr/bin/with-contenv ash
source homecentr_create_group
source homecentr_print_banner
source homecentr_print_context
source homecentr_set_s6_env_var

EXEC_GROUP="root"
EXEC_USER="root"

### Validate inputs
if [ "$PUID" == "" ]
then
  >&2 echo "The PUID variable cannot be empty."
  exit 2
fi

if [ "$PGID" == "" ]
then
  >&2 echo "The PGID variable cannot be empty."
  exit 2
fi

### Create primary group
if [ "$PGID" -ne "0" ]
then
  # The user must be deleted first otherwise it could still exist with differet UID
  cat /etc/passwd | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # User already exists, delete it
    deluser nonroot
  fi

  homecentr_create_group "nonroot" "$PGID"

  EXEC_GROUP="nonroot"
fi

### Create user
if [ "$PUID" -ne "0" ]
then
  adduser -u $PUID -G nonroot -D nonroot
  EXEC_USER="nonroot"
  echo "/home/nonroot" > /var/run/s6/container_environment/HOME
fi

### Create groups
if [[ ! -z $PUID_ADDITIONAL_GROUPS ]]
then
  echo "$PUID_ADDITIONAL_GROUPS" | egrep '^[0-9]+\:[a-zA-Z0-9_-]+(,[0-9]+\:[a-zA-Z0-9_-]+)*$' > /dev/null

  if [ "$?" != "0" ]; then
    >&2 echo "The value $PUID_ADDITIONAL_GROUPS is invalid. Valid format is <gid1>:<group-name1>,<gid2>:<group-name2>"
    exit 2
  fi

  # Expecting string in format "gid:name,gid:name"
  GROUPS=$(echo $PUID_ADDITIONAL_GROUPS | tr "," "\n")

  for GROUP in $GROUPS
  do
    GRP_ID=$(echo $GROUP | cut -d ':' -f 1)
    GRP_NAME=$(echo $GROUP | cut -d ':' -f 2)

    # Create group (delete if already exists)
    homecentr_create_group "$GRP_NAME" "$GRP_ID"

    # Add user to the group
    addgroup "$EXEC_USER" "$GRP_NAME"
  done
fi

homecentr_print_banner

# Write the variable so that other scripts can use it
homecentr_set_s6_env_var "EXEC_USER" "$EXEC_USER"

homecentr_print_context