package me.ele.gradle.release

interface VersionUpgradeStrategy {

    String getVersion(String currentVersion);

}