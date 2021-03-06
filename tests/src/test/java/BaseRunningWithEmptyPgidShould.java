import helpers.BaseDockerImageTagResolver;
import helpers.Image;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.nio.file.Paths;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class BaseRunningWithEmptyPgidShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before() {
        _container = new GenericContainerEx<>(new BaseDockerImageTagResolver())
                .withStartupAttempts(1)
                .withEnv("PGID", "")
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        try {
            _container.start();
        }
        catch (ContainerLaunchException ex) {
            // The container is expected to fail to start
        }

        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @Test
    public void printWarning() throws Exception {
        waitFor(Duration.ofSeconds(5), () -> _container.getLogs(OutputFrame.OutputType.STDERR).contains("PGID variable cannot be empty"));
    }

    @Test
    public void exit() throws Exception {
        waitFor(Duration.ofSeconds(5), () -> !_container.isRunning());
    }
}