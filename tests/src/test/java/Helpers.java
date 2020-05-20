import java.nio.file.Paths;

public class Helpers {
    public final static String dockerImageFallback = "homecentr/base:local-alpine";

    public static String getExamplesDir() {
        return Paths.get(
                "..",
                System.getProperty("base"),
                "example").toString();
    }

    public static String getShell() {
        if(System.getProperty("base") == "alpine") {
            return "ash";
        }

        return "base";
    }
}
