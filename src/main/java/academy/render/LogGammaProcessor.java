package academy.render;

import academy.model.FractalImage;
import academy.model.Pixel;

public class LogGammaProcessor {
    //private final double gamma = 2.2;

    public void process(FractalImage image, double gamma) {
        double max = 0.0;
        int[] hits = image.hitCounts();

        for (int hit : hits) {
            if (hit != 0) {
                max = Math.max(max, Math.log10(hit));
            }
        }

        int[] r = image.red();
        int[] g = image.green();
        int[] b = image.blue();

        for (int i = 0; i < hits.length; i++) {
            if (hits[i] == 0) continue;

            double normal = Math.log10(hits[i]) / max;
            double factor = Math.pow(normal, 1.0 / gamma);

            r[i] = (int) (r[i] * factor);
            g[i] = (int) (g[i] * factor);
            b[i] = (int) (b[i] * factor);
        }
    }


//    public void process(FractalImage image, double gamma) {
//        //int maxHits = 0;
//
//        double maxNormal = 0.0;
//
//        for (int y = 0; y < image.size().height(); ++y) {
//            for (int x = 0; x < image.size().width(); ++x) {
//                Pixel pixel = image.pixel(x, y);
//                if (pixel.getHitCount() != 0) {
//                    double normal = Math.log10(pixel.getHitCount());
//                    if (normal > maxNormal) maxNormal = normal;
//                }
//            }
//        }
//
//        if (maxNormal == 0) return;
//
//        for (int y = 0; y < image.size().height(); ++y) {
//            for (int x = 0; x < image.size().width(); ++x) {
//                Pixel pixel = image.pixel(x, y);
//                if (pixel.getHitCount() != 0) {
//                    double normal = Math.log10(pixel.getHitCount()) / maxNormal;
//                    double factor = Math.pow(normal, 1.0 / gamma);
//
//                    pixel.setR((int) (pixel.getR() * factor));
//                    pixel.setG((int) (pixel.getG() * factor));
//                    pixel.setB((int) (pixel.getB() * factor));
//                }
//            }
//        }
//    }
}
