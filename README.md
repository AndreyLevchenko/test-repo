# Atlassian Automation Plugin

## Overview
This plugin provides x-product automation for Atlassian products. You can read more on [how to use and configure this plugin ](https://blogs.atlassian.com/2014/02/atlassian-support-uses-jira-automation-plugin/).
We've put great effort into making this plugin more extendable by everyone, and there is [tutorial on how to add own actions](http://blogs.atlassian.com/2014/02/extending-jira-automation-plugin/).

## Prerequisities

* Java 8 (JDK)
* Atlassian Plugin SDK


## Getting started

From the top level directory run:

    mvn clean install -DskipTests
    cd automation-func-tests

JIRA: `mvn amps:debug -Dproduct=jira`

`atlas-mvn clean install` will execute all tests in all products.  To execute JIRA tests only add:
`-DtestGroups=jira-integration`


## Branches

Due to breaking changes in JIRA 7, the `stable-1.x` branch is being introduced. This branch contains code compatible with JIRA 6.1-6.x and should be kept compatible for as long as possible. 
Unless you want to create your changes for JIRA 7 only, contribute to this (i.e `stable-1.x`) branch.

`master` branch is compatible with JIRA 7 only (the Automation plugin 2.x versions). I will try my best to keep both branches in sync. 


## Pull requests

Generally pull requests fixing bugs and/or adding new features are welcome. When contributing please follow the coding style (`{` on new lines, 4 spaces indent) and add necessary tests (unit tests and preferably integration/Selenium tests) too.
Please note that each pull requests will need to pass `atlas-mvn clean install` and if it's submitted to the `stable-1.x` then it will be tested against JIRA 6.1.1, 6.3 and latest 6.x release.