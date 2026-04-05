package academy.render;

import academy.config.dto.Size;
import academy.model.FractalImage;
import academy.model.Pixel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LogGammaProcessorTest {

//    @Test
//    @DisplayName("Processor should normalize brightness")
//    void testProcess() {
//        FractalImage image = FractalImage.create(new Size(2, 2));
//
//        Pixel p1 = image.pixel(0, 0);
//        p1.mixColor(100, 100, 100);
//        p1.incrementHitCount();
//
//        Pixel p2 = image.pixel(1, 1);
//        p2.mixColor(0xFF, 0xFF, 0xFF);
//        for (int i = 0; i < 10; ++i) {
//            p2.incrementHitCount();
//        }
//
//        double defaultGamma = 2.2;
//        LogGammaProcessor processor = new LogGammaProcessor();
//        processor.process(image, defaultGamma);
//
//        assertNotEquals(100, p1.getR(), "Red channel should be corrected due to lower relative hit count");
//
//
//    }

    @Test
    void testProcess() {
        FractalImage image = FractalImage.create(2, 2);


        image.add(0, 0, 100, 100, 100);


        for (int i = 0; i < 10; i++) {
            image.add(1, 1, 255, 255, 255);
        }

        LogGammaProcessor processor = new LogGammaProcessor();
        processor.process(image, 2.2);


        Pixel p1 = image.pixel(0, 0);
        assertNotEquals(100, p1.getR(), "Red channel should be corrected");
    }

}
