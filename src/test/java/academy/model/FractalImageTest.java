package academy.model;

import academy.config.dto.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FractalImageTest {
    @Test
    @DisplayName("Create image with correct size")
    void testCreate() {
        //FractalImage image = FractalImage.create(new Size(100, 200));
        FractalImage image = FractalImage.create(100, 100);


        assertEquals(100, image.width());
        assertEquals(100, image.height());
        assertNotNull(image);
        assertEquals(10000, image.red().length);
    }

    @Test
    @DisplayName("Contains should return true for valid coordinates")
    void testContains() {
        FractalImage image = FractalImage.create(10, 10);

        assertTrue(image.contains(0,0));
        assertTrue(image.contains(9,9));
        assertFalse(image.contains(-1, 0));
        assertFalse(image.contains(0, 10));
    }
}
