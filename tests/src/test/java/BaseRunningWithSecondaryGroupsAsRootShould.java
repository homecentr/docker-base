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

public class BaseRunningWithSecondaryGroupsAsRootShould {
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
    public void createSecondaryGroups() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp1:x:8005").getExitCode() == 0);
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp2:x:8006").getExitCode() == 0);
    }

    @Test
    public void addPuidUserToSecondaryGroups() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("getent group grp1").getStdout().contains("root"));
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("getent group grp2").getStdout().contains("root"));
    }

    @Test
    public void loadGroupsToPuidUserContext() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*ID=.*8005\\(grp1\\).*"));
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*ID=.*8006\\(grp2\\).*"));
    }
}
