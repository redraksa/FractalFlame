package academy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    private static final String CONFIG_FILENAME = "test_config_integration.json";
    private static final String OUTPUT_FILENAME = "test_output_integration.png";

    @BeforeEach
    void setUp() {

        System.setProperty("testMode", "true");
    }

    @AfterEach
    void tearDown() throws IOException {

        System.clearProperty("testMode");
        Files.deleteIfExists(Path.of(CONFIG_FILENAME));
        Files.deleteIfExists(Path.of(OUTPUT_FILENAME));
    }

    @Test
    void main_ShouldRunSuccessfully_WhenConfigIsValid() throws IOException {

        String jsonContent = """
            {
              "size": { "width": 100, "height": 100 },
              "iteration_count": 1000,
              "output_path": "%s",
              "threads": 1,
              "seed": 12345.0,
              "functions": [
                { "name": "linear", "weight": 1.0 }
              ],
              "affine_params": [
                { "a": 1, "b": 0, "c": 0, "d": 0, "e": 1, "f": 0, "rgb": [255, 0, 0] }
              ],
              "symmetry_level": 1,
              "gamma": 2.2
            }
            """.formatted(OUTPUT_FILENAME);

        Path configPath = Path.of(CONFIG_FILENAME);
        Files.writeString(configPath, jsonContent);


        String[] args = {"--config=" + CONFIG_FILENAME};
        Application.main(args);


        Path outputPath = Path.of(OUTPUT_FILENAME);


        assertTrue(Files.exists(outputPath), "Output image file should exist");


        assertTrue(Files.size(outputPath) > 0, "Output image file should not be empty");
    }
}
