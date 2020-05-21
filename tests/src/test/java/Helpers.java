import java.nio.file.Paths;

public class Helpers {
    public static String getDockerImageFallback() {
        return "homecentr/base:local-" + getBase();
    }

    public static String getExamplesDir() {
        return Paths.get(
                "..",
                getBase(),
                "example").toString();
    }

    public static String getShell() {
        if(getBase() == "centos") {
            return "bash";
        }

        return "ash";
    }

    private static String getBase() {
        return System.getProperty("base");
    }
}
