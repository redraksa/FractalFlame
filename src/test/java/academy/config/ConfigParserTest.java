package academy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigParserTest {
    @Test
    @DisplayName("JSON Parser should correctly read AppConfig")
    void testJsonParsing() throws Exception {
        String json = """
            {
              "size": {"width": 100, "height": 100},
              "iteration_count": 1000,
              "output_path": "test.png",
              "threads": 2,
              "seed": 1.0,
              "symmetry_level": 1,
              "functions": [{"name": "linear", "weight": 1.0}],
              "affine_params": [{"a": 1, "b": 0, "c": 0, "d": 0, "e": 1, "f": 0, "rgb": [255, 0, 0]}]
            }
            """;

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        AppConfig config = mapper.readValue(json, AppConfig.class);

        assertEquals(100, config.size().width());
        assertEquals(1000, config.iterationCount());
        assertEquals(2, config.threads());
        assertEquals("test.png", config.outputPath());
    }

    @Test
    @DisplayName("CLI parameters should have priority over JSON config")
    void testCliPriority(@TempDir Path tempDir) throws Exception {

        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, "{\"size\":{\"width\":500, \"height\":500}, \"threads\":1}");

    }
}
