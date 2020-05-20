import java.nio.file.Paths;

public class Helpers {
    public final static String dockerImageFallback = "homecentr/base:local-centos";

    public static String getExamplesDir() {
        return Paths.get(
                "..",
                "centos", //System.getProperty("base"),
                "example").toString();
    }

    public static String getShell() {
        if(System.getProperty("base") == "centos") {
            return "ash";
        }

        return "base";
    }
}
