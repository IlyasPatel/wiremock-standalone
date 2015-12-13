package ilyas.patel;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;
import com.google.common.base.Charsets;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.write;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WiremockStandaloneITest {

    private static final String MAPPINGS = "mappings";
    private static final File FILE_SOURCE_ROOT = new File("C:\\development\\wiremock-standalone\\wiremock-standalone-tests\\src\\test\\java\\ilyas\\patel\\build\\standalone-files");
    private WireMockServerRunner runner;
    private WireMockTestClient testClient;

    private static final String MAPPING_REQUEST =
            "{ 													\n" +
                    "	\"request\": {									\n" +
                    "		\"method\": \"GET\",						\n" +
                    "		\"url\": \"/resource/from/file\"			\n" +
                    "	},												\n" +
                    "	\"response\": {									\n" +
                    "		\"status\": 200,							\n" +
                    "		\"body\": \"Body from mapping file\"		\n" +
                    "	}												\n" +
                    "}													";

    @Test
    public void should_start_wiremock_standalone_with_json_data() {
        writeMappingFile("test-mapping-1.json", MAPPING_REQUEST);
        startRunner();
        assertThat(testClient.get("/resource/from/file").content(), is("Body from mapping file"));
    }

    private void startRunner(String... args) {
        runner = new WireMockServerRunner();
        runner.run(argsWithPort(argsWithRecordingsPath(args)));

        int port = runner.port();
        testClient = new WireMockTestClient(port);
        WireMock.configureFor(port);
    }



    private String[] argsWithRecordingsPath(String[] args) {
        List<String> argsAsList = new ArrayList<String>(asList(args));
        if (!argsAsList.contains("--root-dir")) {
            argsAsList.addAll(asList("--root-dir", FILE_SOURCE_ROOT.getPath()));
        }
        return argsAsList.toArray(new String[]{});
    }

    private String[] argsWithPort(String[] args) {
        List<String> argsAsList = new ArrayList<String>(asList(args));
        if (!argsAsList.contains("--port")) {
            argsAsList.addAll(asList("--port", "" + Options.DYNAMIC_PORT));
        }
        return argsAsList.toArray(new String[]{});
    }


    private void writeMappingFile(String name, String contents) {
        writeFile(underFileSourceRoot(underMappings(name)), contents);
    }

    private String underMappings(String name) {
        return MAPPINGS + separator + name;
    }

    private String underFileSourceRoot(String relativePath) {
        return FILE_SOURCE_ROOT + separator + relativePath;
    }

    private void writeFile(String absolutePath, String contents) {
        try {
            File file = new File(absolutePath);
            createParentDirs(file);
            write(contents, file, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
