#!/bin/sh
username=`getent passwd $1 | cut -d: -f1`

usermod --unlock --expiredate '' $username