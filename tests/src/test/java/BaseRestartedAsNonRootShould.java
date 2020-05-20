import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.nio.file.Paths;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;
import static org.junit.Assert.*;

public class BaseRestartedAsNonRootShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before() {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.dockerImageFallback))
                .withEnv("PUID", "7000")
                .withEnv("PGID", "8000")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.getDockerClient().restartContainerCmd(_container.getContainerId()).exec();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void after() {
        _container.close();
    }

    @Test
    public void notFailWhenCreatingNonRootGroup() throws Exception {
        // Wait for the startup manually as the container was restarted, the waiting strategy does not apply
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().contains("[services.d] done", 2));

        assertFalse(_container.getLogsAnalyzer().contains("group 'nonroot' in use")); // Alpine error
        assertFalse(_container.getLogsAnalyzer().contains("'nonroot' already exists")); // CentOS error
    }

    @Test
    public void notFailWhenCreatingNonRootUser() throws Exception {
        // Wait for the startup manually as the container was restarted, the waiting strategy does not apply
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().contains("[services.d] done", 2));

        assertFalse(_container.getLogsAnalyzer().contains("user 'nonroot' in use"));
        assertFalse(_container.getLogsAnalyzer().contains("user 'nonroot' already exists"));
    }
}