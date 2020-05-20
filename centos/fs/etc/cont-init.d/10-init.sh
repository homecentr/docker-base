#!/usr/bin/with-contenv bash

DISPLAY_GROUP="root"
DISPLAY_USER="root"

if [ "$PGID" != "0" ] || [ "$PUID" != "0" ]
then
  if ( ! type "groupadd" > /dev/null; ) || ( ! type "useradd" > /dev/null; )
  then
    >&2 echo "Cannot create user/group, please install shadow package!"
    exit 1
  fi
fi

if [ "$PGID" -ne "0" ]
then
  groupadd -g $PGID nonroot
  DISPLAY_GROUP="nonroot"
fi

if [ "$PUID" -ne "0" ]
then
  useradd -u $PUID -g nonroot nonroot
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