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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class ReleasePlugin implements Plugin<Project> {

    public static final String RELEASE_TASK_GROUP_NAME = 'Release'
    public static final String RELEASE_TASK_NAME = 'release'
    public static final String PREPARE_TASK_NAME = 'prepare'
    public static final String PREPARE_MAJOR_VERSION_TASK_NAME = 'prepareMajorVersion'
    public static final String PREPARE_MINOR_VERSION_TASK_NAME = 'prepareMinorVersion'
    public static final String PREPARE_PATCH_VERSION_TASK_NAME = 'preparePatchVersion'

    public static Map<String, String> RELEASE_TASKS = [
            (RELEASE_TASK_NAME)              : "Creates a tagged non-SNAPSHOT release.",
            (PREPARE_MAJOR_VERSION_TASK_NAME): 'Upgrades to next major SNAPSHOT version',
            (PREPARE_MINOR_VERSION_TASK_NAME): 'Upgrades to next minor SNAPSHOT version',
            (PREPARE_PATCH_VERSION_TASK_NAME): 'Upgrades to next patch SNAPSHOT version',
            (PREPARE_TASK_NAME):               "Same as ${PREPARE_PATCH_VERSION_TASK_NAME}"
    ]

    public static final String RELEASE_EXTENSION_NAME = 'release'

    private static final LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)

    @Override
    void apply(Project project) {
        LOGGER.debug("Registering extension '$RELEASE_EXTENSION_NAME'")
        def releaseExtension = project.extensions.create(RELEASE_EXTENSION_NAME, ReleaseExtension, project)

        RELEASE_TASKS.each {
            String name = it.key
            String description = it.value
            LOGGER.debug("Registering task '$name'")
            def releaseTask = project.tasks.create(name, ReleaseTask.class)
            releaseTask.description = description
            releaseTask.group = RELEASE_TASK_GROUP_NAME
            releaseTask.dependsOn({ releaseExtension.dependsOn })
        }

        def versionFromFile = releaseExtension.versionFile.text.trim()

        project.logger.lifecycle("Eleme Release Plugin: Using version $versionFromFile")
        project.rootProject.allprojects { p ->
            p.version = versionFromFile
        }
    }

}