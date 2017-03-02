#!/bin/bash -x

OPTS="$*"

vncdisplay=""

killvnc() {
    if ! test -z "${vncdisplay}"; then
        echo stopping vncserver on $DISPLAY
        vncserver -kill $vncdisplay 2>&1
    fi
}



displayEnv() {

	echo "---------------------------------------------"
	echo "Displaying Environment Variables"
	echo "---------------------------------------------"
	env
	echo "---------------------------------------------"
}

echo starting vncserver
vncdisplay=$(vncserver -geometry 1600x1200 2>&1 | tee vnc.log | perl -ne '/^New .* desktop is (.*)$/ && print"$1\n"')
sleep 1
if [ -z "$vncdisplay" ]; then
     echo "failed to create a vncserver or get its display identifier"
     exit 2
fi

export DISPLAY=$vncdisplay
echo vncserver started on ${DISPLAY}, resolution 1600x1200

displayEnv

# Move the mouse pointer out of the way
# echo Moving mouse pointer to 10 10.
# xwarppointer abspos 10 10

#Make sure the VNC server is killed always. Why wont you just die!
trap killvnc INT TERM EXIT

if [ -f "${M2_HOME}/bin/mvn" ]; then
  MVN=${M2_HOME}/bin/mvn
else
  MVN=mvn
fi

echo Starting $MVN $OPTS
$MVN $OPTS
MVN_STATUS=$?

exit $MVN_STATUS
