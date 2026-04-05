package academy.render;

import academy.config.AppConfig;
import academy.config.dto.AffineParam;
import academy.config.dto.TransformationFunction;
import academy.math.TransformationCoefficients;
import academy.math.Variations;
import academy.model.FractalImage;
import academy.model.Pixel;
import academy.model.Point;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class SingleThreadedRenderer {
    public void render(FractalImage canvas, AppConfig config, List<TransformationCoefficients> transformations) {
        long longSeed = Double.doubleToRawLongBits(config.seed());
        Random random = new Random(longSeed); // Используем обычный Random для однопотока
        Point currentPoint = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));

        double totalWeight = config.functions().stream().mapToDouble(TransformationFunction::weight).sum();


        for (int i = 0; i < 20; i++) {
            TransformationCoefficients coeff = transformations.get(random.nextInt(transformations.size()));
            currentPoint = coeff.affineTransformation().apply(currentPoint);
            currentPoint = applyVariations(currentPoint, config.functions(), random, totalWeight);
        }

        for (int i = 0; i < config.iterationCount(); i++) {
            TransformationCoefficients coeff = transformations.get(random.nextInt(transformations.size()));
            currentPoint = coeff.affineTransformation().apply(currentPoint);
            currentPoint = applyVariations(currentPoint, config.functions(), random, totalWeight);


            for (int sym = 0; sym < config.symmetry(); ++sym) {
                double theta = 2 * Math.PI * sym / config.symmetry();
                Point rotated = rotate(currentPoint, theta);
                mapPointToCanvas(canvas, rotated, coeff.color());
            }
        }
    }

    private void mapPointToCanvas(FractalImage canvas, Point p, Color color) {
        double xMin = -1.77, xMax = 1.77;
        double yMin = -1.0, yMax = 1.0;

        if (p.x() >= xMin && p.x() <= xMax && p.y() >= yMin && p.y() <= yMax) {
            int x = (int) ((p.x() - xMin) / (xMax - xMin) * canvas.width());
            int y = (int) ((p.y() - yMin) / (yMax - yMin) * canvas.height());

            if (canvas.contains(x, y)) {

                canvas.add(x, y, color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }


    private Point applyVariations(Point p, List<TransformationFunction> functions, Random random, double totalWeight) {
        double r = random.nextDouble() * totalWeight;
        double currentSum = 0;
        for (TransformationFunction func : functions) {
            currentSum += func.weight();
            if (r <= currentSum) {
                return VariationRegistry.get(func.name()).apply(p);
            }
        }
        return p;
    }

    private Point rotate(Point p, double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Point(p.x() * cos - p.y() * sin, p.x() * sin + p.y() * cos);
    }

//    private Point applyVariations(Point p, List<TransformationFunction> functions) {
//        String functionName = functions.get(0).name();
//        return switch (functionName.toLowerCase()) {
//            case "sinusoidal" -> Variations.sinusoidal().apply(p);
//            case "spherical" -> Variations.spherical().apply(p);
//            case "swirl" -> Variations.swirl().apply(p);
//            default -> Variations.linear().apply(p);
//        };
//    }
}
