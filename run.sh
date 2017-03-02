#!/bin/bash -x
VNC_MAVEN="./maven-with-vnc.sh"
if [ ! -x $VNC_MAVEN ]; then
	chmod +x $VNC_MAVEN
fi
$VNC_MAVEN "$@"
