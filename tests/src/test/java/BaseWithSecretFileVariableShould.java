import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.nio.file.Paths;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseWithSecretFileVariableShould {

    private static final Logger logger = LoggerFactory.getLogger(BaseRunningAsRootShould.class);

    private GenericContainerEx _container;

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void setEnvVarFromSecretFileWhenEnvVarPassed() throws Exception {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.getDockerImageFallback()))
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "var-value.txt").toString(), "/var-value.txt")
                .withEnv("FILE__TEST_VAR", "/var-value.txt")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();

        waitFor(
                Duration.ofSeconds(10),
                () -> _container.getLogsAnalyzer().contains("TEST_VAR=Hello, world!"));
    }

    @Test
    public void printToOutputThatVariableWasSetWhenEnvVarPassed() throws Exception {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.getDockerImageFallback()))
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "var-value.txt").toString(), "/var-value.txt")
                .withEnv("FILE__TEST_VAR", "/var-value.txt")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();

        assertTrue(_container.getLogsAnalyzer().contains("[env-vars] Variable TEST_VAR set from /var-value.txt"));
    }

    @Test
    public void printToOutputWhenPassedFileDoesNotExist() throws Exception {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.getDockerImageFallback()))
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "var-value.txt").toString(), "/var-value.txt")
                .withEnv("FILE__TEST_VAR", "/not-existing.txt")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        assertTrue(_container.getLogsAnalyzer().contains("[env-vars] Variable TEST_VAR could not be set from /not-existing.txt. File not found."));
    }

    @Test
    public void skipWhenNoVariablesPassed() {
        _container = new GenericContainerEx<>(new EnvironmentImageTagResolver(Helpers.getDockerImageFallback()))
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withRelativeFileSystemBind(Paths.get(Helpers.getExamplesDir(), "var-value.txt").toString(), "/var-value.txt")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        assertFalse(_container.getLogs().contains("[env-vars]"));
    }
}