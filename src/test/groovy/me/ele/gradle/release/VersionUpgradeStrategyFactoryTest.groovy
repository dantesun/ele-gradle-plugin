package me.ele.gradle.release

import org.gradle.api.GradleException
import org.junit.Test

import static org.junit.Assert.assertEquals

class VersionUpgradeStrategyFactoryTest {

    private static final String SUFFIX = '-SNAPSHOT'

    @Test
    public void parseVersionInfo() {
        def info = VersionUpgradeStrategyFactory.parseVersionInfo("1.2.3")
        assertEquals(1, info.major)
        assertEquals(2, info.minor)
        assertEquals(3, info.patch)
    }

    @Test(expected = GradleException)
    public void parseVersionInfo_wrongPattern() {
        VersionUpgradeStrategyFactory.parseVersionInfo("42")
    }

    @Test
    public void createMajorVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMajorVersionUpgradeStrategy(SUFFIX);
        assertEquals("2.0.0-SNAPSHOT", strategy.getVersion("1.2.3-SNAPSHOT"))
    }

    @Test
    public void createMajorVersionUpgradeFromReleaseStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMajorVersionUpgradeStrategy(SUFFIX);
        assertEquals("2.0.0-SNAPSHOT", strategy.getVersion("1.2.3"))
    }

    @Test
    public void createMinorVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMinorVersionUpgradeStrategy(SUFFIX);
        assertEquals("1.3.0-SNAPSHOT", strategy.getVersion("1.2.3-SNAPSHOT"))
    }

    @Test
    public void createMinorVersionUpgradeFromReleaseStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMinorVersionUpgradeStrategy(SUFFIX);
        assertEquals("1.3.0-SNAPSHOT", strategy.getVersion("1.2.3"))
    }

    @Test
    public void createPatchVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createPatchVersionUpgradeStrategy(SUFFIX);
        assertEquals("1.2.4-SNAPSHOT", strategy.getVersion("1.2.3-SNAPSHOT"))
    }

    @Test
    public void createPatchVersionUpgradeFromReleaseStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createPatchVersionUpgradeStrategy(SUFFIX);
        assertEquals("1.2.4-SNAPSHOT", strategy.getVersion("1.2.3"))
    }

    @Test
    public void createReleaseVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createReleaseVersion(SUFFIX);
        assertEquals("1.2.3", strategy.getVersion("1.2.3-SNAPSHOT"))
    }

    @Test
    public void createReleaseVersionFromReleaseUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createReleaseVersion(SUFFIX);
        assertEquals("1.2.3", strategy.getVersion("1.2.3"))
    }

    @Test
    public void createSnapshot() {
        def strategy = VersionUpgradeStrategyFactory.createCurrentVersion();
        assertEquals("1.2.3-SNAPSHOT", strategy.getVersion("1.2.3-SNAPSHOT"))
    }

}
