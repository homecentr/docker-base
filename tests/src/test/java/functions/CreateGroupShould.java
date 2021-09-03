package functions;

import helpers.BaseDockerImageTagResolver;
import helpers.Image;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.Container;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CreateGroupShould {
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
    public void returnExitCodeZero() throws IOException, InterruptedException {
        Container.ExecResult result = _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9999 grp1");

        assertEquals(0, result.getExitCode());
    }

    @Test
    public void createGroup() throws IOException, InterruptedException {
        _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9999 grp1");

        assertGroupExists(9999, "grp1");
    }

    @Test
    public void overwriteGroupWhenGroupWithSameGidAndDifferentNameExists() throws IOException, InterruptedException {
        _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9999 grp1");
        Container.ExecResult res1 = _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9999 grp2");

        assertGroupExists(9999, "grp2");
    }

    @Test
    public void overwriteGroupWhenGroupWithDifferentGidAndSameNameExists() throws IOException, InterruptedException {
        _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9999 grp1");
        Container.ExecResult res1 = _container.executeShellCommand("source homecentr_create_group && homecentr_create_group 9998 grp1");

        assertGroupExists(9998, "grp1");
    }

    private void assertGroupExists(int gid, String name) throws IOException, InterruptedException {
        Container.ExecResult result = _container.executeShellCommand("getent group " + name);
        String[] groupRecord = result.getStdout().trim().split(":");

        assertEquals(name, groupRecord[0]);
        assertEquals(gid, Integer.parseInt(groupRecord[2]));
    }
}
