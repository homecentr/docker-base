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

public class BaseRunningWithSecondaryGroupsAsNonRootShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before() {
        _container = new GenericContainerEx<>(new BaseDockerImageTagResolver())
                .withEnv("PUID", "7000")
                .withEnv("PGID", "8000")
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
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp1:x:8005").getStdout().contains("grp1"));
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp2:x:8006").getStdout().contains("grp2"));
    }

    @Test
    public void addPuidUserToSecondaryGroups() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp1:x:8005").getStdout().contains("nonroot"));
        waitFor(Duration.ofSeconds(10), () -> _container.executeShellCommand("cat /etc/group | grep ^grp2:x:8006").getStdout().contains("nonroot"));
    }

    @Test
    public void loadGroupsToPuidUserContext() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*ID=.*8005\\(grp1\\).*"));
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*ID=.*8006\\(grp2\\).*"));
    }
}
