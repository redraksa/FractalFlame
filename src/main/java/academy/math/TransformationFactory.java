package academy.math;

import academy.config.AppConfig;
import academy.config.dto.AffineParam;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransformationFactory {
    public static List<TransformationCoefficients> prepareTransformations(AppConfig config) {
        List<TransformationCoefficients> result = new ArrayList<>();
        long longSeed = Double.doubleToRawLongBits(config.seed());
        Random random = new Random(longSeed);

        for (AffineParam p: config.affineParams()) {
            AffineTransformation at = new AffineTransformation(p.a(), p.b(), p.c(), p.d(), p.e(), p.f());

            Color color;
            if (p.rgb() != null && p.rgb().length == 3) {
                color = new Color(p.rgb()[0], p.rgb()[1], p.rgb()[2]);
            } else {
                color = new Color(random.nextInt(0xFF), random.nextInt(0xFF), random.nextInt(0xFF));
            }
            result.add(new TransformationCoefficients(at, color));
        }

        return result;
    }
}
