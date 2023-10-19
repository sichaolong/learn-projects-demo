#!/bin/bash

echo "=======发布新版本======="

currentBranch=`git branch |grep "*"|awk '{print $2}'`
echo "将当前代码改动暂存到stash，以便发版完成后恢复"
stashRlt=`git stash`

# 清理一下已删除的分支
git remote prune origin

echo "从远端拉取最新的master分支的代码"
git checkout master && git pull origin master

# mvn命令获取当前项目的版本号（性能有点差）
masterVersion=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout`
# 去掉末尾的-SNAPSHOT
autoReleaseVersion=${masterVersion%-SNAPSHOT*}

echo "master当前版本号：$masterVersion"
read -p "请输入发布版本号: [默认：$autoReleaseVersion]" releaseVersion
if [ "$releaseVersion" == "" ]; then
  releaseVersion=$autoReleaseVersion
fi

# 因为后续要以版本号来打tag，所以这里先检查一下tag是否已存在，防止后续提交失败
while [ "`git tag -l $releaseVersion`" != "" ]; do
  read -p "tag $releaseVersion 已存在，请使用新的版本号:" releaseVersion
done

echo "创建release分支"
releaseBranch="release-$releaseVersion"
git branch $releaseBranch && git checkout $releaseBranch

echo "修改pom版本"
mvn versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false && \
git commit -am "updating poms for $releaseVersion" && \
git push origin $releaseBranch

echo "创建tag $releaseVersion"
git tag $releaseVersion

echo "发布到maven仓库"
mvn clean deploy -Dmaven.test.skip=true

autoNewMasterVersion="${releaseVersion%.*}.`expr ${releaseVersion##*.} + 1`-SNAPSHOT"
read -p "请输入master新版本号: [默认：$autoNewMasterVersion]" newMasterVersion
if [ "$newMasterVersion" == "" ]; then
  newMasterVersion=$autoNewMasterVersion
fi

echo "修改pom版本，并推送到远端"
git checkout master && \
git merge $releaseBranch && \
mvn versions:set -DnewVersion=$newMasterVersion -DgenerateBackupPoms=false && \
git commit -am "updating poms for $newMasterVersion" && \
git push origin master --tags

if [ "$stashRlt" != "No local changes to save" ]; then
  echo "git暂存区有暂存的代码，可以使用git stash pop恢复"
fi

read -n1 -p "发版结束"

