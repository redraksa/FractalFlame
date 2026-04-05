package academy.utils;

import academy.config.dto.Size;
import academy.model.FractalImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageUtilsTest {
    @Test
    @DisplayName("Should save image to disk")
    void testSave(@TempDir Path tempDir) {
//        FractalImage image = FractalImage.create(new Size(100, 100));
//        image.pixel(50, 50).mixColor(0xFF, 0x00, 0x00);
//
//        Path outputFile = tempDir.resolve("test_output.png");
//
//        ImageUtils.save(image, outputFile);
//
//        assertTrue(Files.exists(outputFile), "File should be created");
//        assertTrue(Files.isRegularFile(outputFile));

    }

    @Test
    void save_CreatesFile() throws Exception {
        FractalImage image = FractalImage.create(10, 10);
        Path path = Path.of("utils_test.png");

        ImageUtils.save(image, Path.of(path.toString()));

        assertTrue(Files.exists(path));
        Files.deleteIfExists(path);
    }
}
