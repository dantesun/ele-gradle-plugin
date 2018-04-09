package ch.netzwerg.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

public class ReleaseExtensionTest {

    private ReleaseExtension extension

    @Before
    public void before() {
        Project project = ProjectBuilder.builder().build();
        project.version = '1.2.3.DEV'
        extension = new ReleaseExtension(project);
        extension.setVersionSuffix('.DEV')
    }

    @Test
    public void getTagName() {
        assertEquals('v1.2.3', extension.tagName)
    }

    @Test
    public void getTagNameWithPrefix() {
        extension.setTagPrefix('myPrefix_')
        assertEquals('myPrefix_1.2.3', extension.tagName)
    }

    @Test
    public void versionFile() {
        assertNotNull(extension.versionFile)
        assertEquals('version.txt', extension.versionFile.getName())
    }

}