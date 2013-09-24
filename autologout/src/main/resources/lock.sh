#!/bin/sh
username=`getent passwd $1 | cut -d: -f1`

usermod --lock --expiredate 1970-01-01 $username