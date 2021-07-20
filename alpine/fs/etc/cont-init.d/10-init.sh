#!/usr/bin/with-contenv ash
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

### Verify required packages are available
if [ "$PGID" != "0" ] || [ "$PUID" != "0" ]
then
  if ( ! type "addgroup" > /dev/null; ) || ( ! type "adduser" > /dev/null; )
  then
    >&2 echo "Cannot create user/group, please install shadow package!"
    exit 1
  fi
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

  # Check if the group exists already
  cat /etc/group | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # Group already exists, delete it
    delgroup nonroot
  fi
  
  addgroup -g $PGID nonroot
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
  # Expecting string in format "gid:name,gid:name"
  GROUPS=$(echo $PUID_ADDITIONAL_GROUPS | tr "," "\n")

  for GROUP in $GROUPS
  do
    GRP_ID=$(echo $GROUP | cut -d ':' -f 1)
    GRP_NAME=$(echo $GROUP | cut -d ':' -f 2)

    # Create group
    addgroup -g "$GRP_ID" "$GRP_NAME"

    # Add user to the group
    addgroup "$EXEC_USER" "$GRP_NAME"
  done
fi

echo '
    __  __                                     __
   / / / /___  ____ ___  ___  ________  ____  / /______
  / /_/ / __ \/ __ `__ \/ _ \/ ___/ _ \/ __ \/ __/ ___/
 / __  / /_/ / / / / / /  __/ /__/  __/ / / / /_/ /    
/_/ /_/\____/_/ /_/ /_/\___/\___/\___/_/ /_/\__/_/     
'
echo "
-------------------------------------
User uid:    $(id -u $EXEC_USER)
User gid:    $(id -g $EXEC_GROUP)
-------------------------------------
"

# Write the variable so that the runas script can use it
echo "$EXEC_USER" > /var/run/s6/container_environment/EXEC_USER