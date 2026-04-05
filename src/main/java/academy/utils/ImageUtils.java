package academy.utils;

import academy.model.FractalImage;
import academy.model.Pixel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class ImageUtils {
    public static void save(FractalImage fractalImage, Path path) {
        int width = fractalImage.width();
        int height = fractalImage.height();

        BufferedImage bufferedImage = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_BGR
        );

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Pixel p = fractalImage.pixel(x, y);

                int rgb = (p.getR() << 16) | (p.getG() << 8) | (p.getB());
                //int rgb = (p.getHitCount() > 0) ? 0xFFFFFF : 0x000000;
                bufferedImage.setRGB(x, y, rgb);
            }
        }

        try {
            ImageIO.write(bufferedImage, "png", path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
