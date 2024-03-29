import helpers.BaseDockerImageTagResolver;
import helpers.Image;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
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

public class BaseRunningAsRootShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before() {
        _container = new GenericContainerEx<>(new BaseDockerImageTagResolver())
            .withEnv("PUID", "0")
            .withEnv("PGID", "0")
            .withEnv("PUID_ADDITIONAL_GROUPS", "8005:grp1,8006:grp2")
            .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
            .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
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
    public void writeZeroPgidIntoOutput() throws Exception {
        waitFor(
                Duration.ofSeconds(10),
                () -> _container.getLogsAnalyzer().matches(".*User gid:\\s+0.*"));
    }

    @Test
    public void writeZeroPuidIntoOutput() throws Exception {
        waitFor(
                Duration.ofSeconds(10),
                () -> _container.getLogsAnalyzer().matches(".*User uid:\\s+0.*"));
    }

    @Test
    public void runServiceAsRootUid() throws Exception {
        int uid = _container.getProcessUid(Image.getShell() + " /usr/sbin/loop");

        assertEquals(0, uid);
    }

    @Test
    public void runServiceAsRootGid() throws Exception {
        int gid = _container.getProcessGid(Image.getShell() + " /usr/sbin/loop");

        assertEquals(0, gid);
    }

    @Test
    public void updateHomeEnvironmentVariable() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().contains("HOME=/root"));
    }
}