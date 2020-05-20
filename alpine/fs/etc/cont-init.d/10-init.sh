#!/usr/bin/with-contenv ash
DISPLAY_GROUP="root"
DISPLAY_USER="root"

if [ "$PGID" != "0" ] || [ "$PUID" != "0" ]
then
  if ( ! type "addgroup" > /dev/null; ) || ( ! type "adduser" > /dev/null; )
  then
    >&2 echo "Cannot create user/group, please install shadow package!"
    exit 1
  fi
fi

if [ "$PGID" -ne "0" ]
then
  # The user must be deleted first otherwise it could still be a m
  cat /etc/passwd | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # User already exists, delete it
    deluser nonroot
  fi

  cat /etc/group | grep ^nonroot: > /dev/null

  if [ $? == 0 ]
  then
    # Group already exists, delete it
    delgroup nonroot
  fi
  
  addgroup -g $PGID nonroot
  DISPLAY_GROUP="nonroot"
fi

if [ "$PUID" -ne "0" ]
then
  adduser -u $PUID -G nonroot -D nonroot
  DISPLAY_USER="nonroot"
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