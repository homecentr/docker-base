import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.nio.file.Paths;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;
import static org.junit.Assert.assertEquals;

public class BaseRunningAsNonRootShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before() {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.getDockerImageFallback()))
                .withEnv("PUID", "7000")
                .withEnv("PGID", "8000")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void after() {
        _container.close();
    }

    @Test
    public void writePgidIntoOutput() throws Exception {
        waitFor(
                Duration.ofSeconds(10),
                () -> _container.getLogsAnalyzer().matches(".*User gid:\\s+8000.*"));
    }

    @Test
    public void writePuidIntoOutput() throws Exception {
        waitFor(
                Duration.ofSeconds(10),
                () -> _container.getLogsAnalyzer().matches(".*User uid:\\s+7000.*"));
    }

    @Test
    public void runServiceAsPassedUid() throws Exception {
        int uid = _container.getProcessUid(Helpers.getShell() + " /usr/sbin/loop");

        assertEquals(7000, uid);
    }

    @Test
    public void runServiceAsPassedGid() throws Exception {
        int gid = _container.getProcessGid(Helpers.getShell() + " /usr/sbin/loop");

        assertEquals(8000, gid);
    }
}