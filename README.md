This is a optioned plugin for Eleme Inc. Initial purpose is to support daily development
in Eleme by define a preset of all kinds of configurations and plugin. 
But it is still served as a general purpose plugin.

It steals code from the following plugins:
* https://github.com/netzwerg/gradle-release-plugin

Usage
-------------
* [Gradle Plugin Portal](https://plugins.gradle.org/plugin/me.ele.gradle)
* only apply this plugin to `root` project
* version number are kept in `root` project directory's `version.txt`
* When this plugin is applied, it will set all the projects' version from `version.txt`
* `gradle release` will strip the `SNAPSHOT` suffix and create a tag
* `gradle prepare` will modifies `version.txt`, increase the version number's patch part
* after `release/prepare`, use `git push` to push the changes
* prepareXXX tasks always append -SNAPSHOT suffix and always increases the version number

Release tasks
-------------
* release - Creates a tagged non-SNAPSHOT release.
* prepare - Same as preparePatchVersion
* prepareMajorVersion - Upgrades to next major SNAPSHOT version
* prepareMinorVersion - Upgrades to next minor SNAPSHOT version
* preparePatchVersion - Upgrades to next patch SNAPSHOT version
