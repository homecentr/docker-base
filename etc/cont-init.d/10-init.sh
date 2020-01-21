#!/usr/bin/with-contenv ash
PUID=${PUID:-7077}
PGID=${PGID:-7077}

if ( ! type "groupadd" > /dev/null;) || (! type "useradd" > /dev/null; )
then
   >&2 echo "Cannot create user, please install shadow package!"
   exit 1
fi

addgroup -g $PGID nonroot
adduser -u $PUID -G nonroot -D nonroot

echo '
    __  __                                     __
   / / / /___  ____ ___  ___  ________  ____  / /______
  / /_/ / __ \/ __ `__ \/ _ \/ ___/ _ \/ __ \/ __/ ___/
 / __  / /_/ / / / / / /  __/ /__/  __/ / / / /_/ /    
/_/ /_/\____/_/ /_/ /_/\___/\___/\___/_/ /_/\__/_/     
'
echo "
-------------------------------------
User uid:    $(id -u nonroot)
User gid:    $(id -g nonroot)
-------------------------------------
"