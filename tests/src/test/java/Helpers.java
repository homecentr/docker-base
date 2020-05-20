import java.nio.file.Paths;

public class Helpers {
    public static String getDockerImageFallback() {
        return "homecentr/base:local-centos";
    }

    public static String getExamplesDir() {
        return Paths.get(
                "..",
                System.getProperty("base"),
                "example").toString();
    }

    public static String getShell() {
        if(System.getProperty("base") == "centos") {
            return "ash";
        }

        return "base";
    }
}
