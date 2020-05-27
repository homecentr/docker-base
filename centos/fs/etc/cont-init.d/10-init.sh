#!/usr/bin/with-contenv bash

echo "PUID=$PUID, PGID=$PGID"

DISPLAY_GROUP="root"
DISPLAY_USER="root"

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

if [ "$PGID" != "0" ] || [ "$PUID" != "0" ]
then
  if ( ! type "groupadd" > /dev/null; ) || ( ! type "useradd" > /dev/null; )
  then
    >&2 echo "Cannot create user/group, please install shadow package!"
    exit 1
  fi
fi

if [ "$PGID" != "0" ]
then
  # The user must be deleted first otherwise it could still be a m
  cat /etc/passwd | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # User already exists, delete it
    userdel nonroot
  fi

  cat /etc/group | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # Group already exists, delete it
    groupdel nonroot
  fi

  groupadd -g $PGID nonroot
  DISPLAY_GROUP="nonroot"
fi

if [ "$PUID" != "0" ]
then
  useradd -u $PUID -g $PGID nonroot
  DISPLAY_USER="nonroot"
  
  echo "/home/nonroot" > /var/run/s6/container_environment/HOME
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
User uid:    $(id -u $DISPLAY_USER)
User gid:    $(id -g $DISPLAY_GROUP)
-------------------------------------
"