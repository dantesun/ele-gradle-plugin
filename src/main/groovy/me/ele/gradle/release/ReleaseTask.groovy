/**
 * Copyright 2014 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.ele.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

import static me.ele.gradle.release.ReleasePlugin.*

class ReleaseTask extends DefaultTask {

    private static final LOGGER = LoggerFactory.getLogger(ReleaseTask.class)

    static final RELEASE_TASK_DESC = 'Creates a tagged non-SNAPSHOT release.'

    ReleaseTask() {
        description = RELEASE_TASK_DESC
    }

    private
    VersionUpgradeStrategy resolveVersionUpgradeStrategy(String versionSuffix) {
        switch (name) {
            case PREPARE_MAJOR_VERSION_TASK_NAME:
                return VersionUpgradeStrategyFactory.createMajorVersionUpgradeStrategy(versionSuffix)
            case PREPARE_MINOR_VERSION_TASK_NAME:
                return VersionUpgradeStrategyFactory.createMinorVersionUpgradeStrategy(versionSuffix)
            case PREPARE_PATCH_VERSION_TASK_NAME:
            case PREPARE_TASK_NAME:
                return VersionUpgradeStrategyFactory.createPatchVersionUpgradeStrategy(versionSuffix)
            case RELEASE_TASK_NAME:
                return VersionUpgradeStrategyFactory.createReleaseVersion(versionSuffix)
        }
        return null
    }

    @TaskAction
    def release() {
        checkUncommittedChanges()
        ReleaseExtension releaseExtension = project.getExtensions().getByType(ReleaseExtension.class)
        VersionUpgradeStrategy upgradeStrategy = resolveVersionUpgradeStrategy(releaseExtension.versionSuffix)
        assert null != upgradeStrategy: "Unable to determine version strategy!"

        def currentVersion = releaseExtension.versionFile.text.trim()
        def releaseVersion = upgradeStrategy.getVersion(currentVersion)
        assert currentVersion != releaseVersion: "Version upgrade is failed!"

        if (project.gradle.startParameter.dryRun) {
            project.logger.lifecycle("Writing release version '$releaseVersion' to file '$releaseExtension.versionFile'")
        } else {
            LOGGER.debug("Writing release version '$releaseVersion' to file '$releaseExtension.versionFile'")
            releaseExtension.versionFile.text = releaseVersion
        }

        commitVersionFile("Release v$project.version", releaseExtension)
        if (RELEASE_TASK_NAME == name) {
            createReleaseTag(releaseExtension.tagName)
        }
        if (releaseExtension.push) {
            pushChanges(releaseExtension.tagName)
        }
    }

    def checkUncommittedChanges() {
        git('diff', '--exit-code')({ "GIT repository has changes!" })
        git('diff', '--cached', '--exit-code')({ "GIT repository has un-committed changes!" })
    }

    def commitVersionFile(String msg, ReleaseExtension releaseExtension) {
        LOGGER.debug("Committing version file: $msg")
        git('commit', '-m', msg, releaseExtension.versionFile.name)({
            "Unable to commit version file!"
        })
    }

    def createReleaseTag(String tagName) {
        project.logger.lifecycle("Create Tag $tagName")
        git('tag', '-a', tagName, "-m Release $tagName")({ "Failed to create tag!" })
    }

    def static getNextVersion(String currentVersion, String suffix) {
        def versionInfo = VersionUpgradeStrategyFactory.parseVersionInfo(currentVersion - suffix)
        int nextPatch = versionInfo.patch + 1
        "$versionInfo.major.$versionInfo.minor.$nextPatch$suffix" as String
    }

    def pushChanges(String tag) {
        LOGGER.debug('Pushing changes to repository')
        git('push', 'origin', tag)({ "Failed to push tag!" })
        git('push', 'origin', 'HEAD')({ "Failed to push HEAD!" })
    }

    Closure<?> git(Object... arguments) {
        if (project.gradle.startParameter.dryRun) {
            project.logger.lifecycle("git ${arguments.join(' ')}")
        }
        def (gitOutput, result) = exec(arguments)
        if (!gitOutput.isEmpty()) {
            project.logger.info(gitOutput)
        }
        return { Closure<?> errorHint ->
            if (result.exitValue != 0) {
                project.logger.error("${errorHint.call(result)}(git ${arguments.join(' ')})")
                result.assertNormalExitValue()
            }
        }
    }

    def exec(Object[] arguments) {
        LOGGER.debug("git $arguments")
        def output = new ByteArrayOutputStream()
        def result = project.exec {
            executable 'git'
            args arguments
            standardOutput output
            ignoreExitValue = true
        }
        // output result to debug
        String gitOutput = output.toString().trim()
        return [gitOutput, result]
    }

}
