package functions;

import helpers.BaseDockerImageTagResolver;
import helpers.Image;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.*;
import org.testcontainers.containers.Container;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class GetSetS6VarShould {
    private GenericContainerEx _container;

    @Before
    public void before() {
        _container = new GenericContainerEx<>(new BaseDockerImageTagResolver())
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "loop").toString(), "/usr/sbin/loop")
                .withRelativeFileSystemBind(Paths.get(Image.getExamplesDir(), "run").toString(), "/etc/services.d/env-test/run")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();
    }

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnEmptyStringWhenVariableNotDefined() throws IOException, InterruptedException {
         Container.ExecResult result = _container.executeShellCommand("source homecentr_get_s6_env_var && homecentr_get_s6_env_var \"dummy_var\"");

         assertEquals(0, result.getExitCode());
         assertEquals("", result.getStdout());
    }

    @Test
    public void returnSetVariable() throws IOException, InterruptedException {
        Container.ExecResult setResult = _container.executeShellCommand("source homecentr_set_s6_env_var && homecentr_set_s6_env_var \"dummy_var\" \"dummy_value\"");

        Container.ExecResult getResult = _container.executeShellCommand("source homecentr_get_s6_env_var && homecentr_get_s6_env_var \"dummy_var\"");

        assertEquals(0, setResult.getExitCode());
        assertEquals(0, getResult.getExitCode());
        assertEquals("dummy_value", getResult.getStdout().trim());
    }
}
