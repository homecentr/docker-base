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
import org.testcontainers.containers.ContainerLaunchException;

import java.nio.file.Paths;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class BaseRunningWithInvalidSecondaryGroupsAsRootShould {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void before(){
        _container = new GenericContainerEx<>(new BaseDockerImageTagResolver())
                .withEnv("PUID", "0")
                .withEnv("PGID", "0")
                .withEnv("PUID_ADDITIONAL_GROUPS", "8005=grp1") // valid delimiter is :
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        try
        {
            _container.start();
        }
        catch (ContainerLaunchException ex) {
            // Expected
        }
    }

    @AfterClass
    public static void after() {
        _container.close();
    }

    @Test
    public void printErrorMessage() throws Exception {
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*Valid format is <gid1>:<group-name1>,<gid2>:<group-name2>.*"));
    }

    @Test
    public void exitFromInitScript() throws Exception {
        // This checks the container stopped on the init script and not because of other failure
        waitFor(Duration.ofSeconds(10), () -> _container.getLogsAnalyzer().matches(".*\\[cont\\-init\\.d\\] 10-init\\.sh: exited.*"));
    }

    @Test
    public void exit() throws Exception {
        waitFor(Duration.ofSeconds(5), () -> !_container.isRunning());
    }
}
