package helpers;

import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;

public class BaseDockerImageTagResolver extends EnvironmentImageTagResolver {
    public BaseDockerImageTagResolver() {
        super("homecentr/base:local");
    }
}
