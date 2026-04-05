package academy.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PixelTest {

//    @Test
//    @DisplayName("First hit should set color directly")
//    void testFirstHit() {
//        Pixel pixel = new Pixel(0,0,0,0);
//
//        pixel.mixColor(0xFF, 0x00, 0x00);
//
//        assertEquals(0xFF, pixel.getR());
//        assertEquals(0x00, pixel.getG());
//        assertEquals(1, pixel.getHitCount());
//    }
//
//    @Test
//    @DisplayName("Subsequent hits should average the color")
//    void testMixin() {
//        Pixel pixel = new Pixel(0xFF, 0x00, 0x00, 1);
//
//        pixel.mixColor(0x00, 0x00, 0xFF);
//
//        assertEquals(127, pixel.getR());
//        assertEquals(127, pixel.getB());
//
//    }


    @Test
    void testMixinInFractalImage() {

        FractalImage image = FractalImage.create(1, 1);


        image.add(0, 0, 255, 0, 0);

        Pixel p1 = image.pixel(0, 0);
        assertEquals(255, p1.getR());
        assertEquals(1, p1.getHitCount());


        image.add(0, 0, 0, 0, 255);

        Pixel p2 = image.pixel(0, 0);
        assertEquals(127, p2.getR(), "Red should be averaged");
        assertEquals(127, p2.getB(), "Blue should be averaged");
        assertEquals(2, p2.getHitCount());
    }

}
