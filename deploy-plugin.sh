#!/usr/bin/env bash

# $1 - release version for jlv eclipse plugin
# $2 - next snapshot version
# E.g.: ./deploy.sh 1.0.0 1.0.1

function check_status() {
	if [ "$1" -gt 0 ]; then
    	  echo "[BUILD FAILED] during $2 phase"
    	  exit $1
	fi
}

rm -fr repo
mkdir repo && cd ./repo
git init
git remote add origin git@github.com:rdiachenko/jlv.git
git fetch origin repo:refs/remotes/origin/repo
git checkout repo
check_status $? '[repo init]'

git rm -fr eclipse

cd ../jlv-eclipse-plugin && mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$1
check_status $? "[setting up jlv-eclipse-plugin release version to $1]"

mvn clean install
check_status $? '[jlv-eclipse-plugin build]'

cd ./com.rdiachenko.jlv.update && mvn wagon:upload
check_status $? '[jlv-eclipse-plugin deploy]'

mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$2-SNAPSHOT
check_status $? '[setting up jlv-eclipse-plugin next snapshot version to $2-SNAPSHOT]'

cd ../

echo "cd ./repo && git add . && git commit -m \"jlv released $1\" && git push origin repo"
echo "git add . && git commit -m \"jlv-eclipse-plugin version was bumped to $2-SNAPSHOT\" && git push"
echo "git tag -a plugin-$1"
echo "git push --tags"