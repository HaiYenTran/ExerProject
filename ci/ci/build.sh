#!/bin/bash -e

#go to becrux project directory
cd "$(dirname "$0")/becrux"

mvn clean

#print automated scenario log in jenkins console
tail -F target/surefire-reports/becrux.automated.AutomatedScenarioRunnerTest-output.txt 2>/dev/null &
tail_pid=$!

#maven install (do not exit if error occurred)
mvn install -Dtest=**.*Tests.java,**.*Spec.java,**/AutomatedScenarioRunnerTest.java -Dfindbugs.skip=true -Denforcer.skip=true -Djacoco.skip=true -Dmaven.test.failure.ignore=true && err= || err=$?

#terminate tail process and exit with maven install status
kill $tail_pid
exit $err
