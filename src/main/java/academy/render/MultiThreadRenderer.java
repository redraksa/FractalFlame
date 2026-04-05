package academy.render;

import academy.config.AppConfig;
import academy.config.dto.TransformationFunction;
import academy.math.TransformationCoefficients;
import academy.model.FractalImage;
import academy.model.Pixel;
import academy.model.Point;
import org.slf4j.LoggerFactory;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiThreadRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadRenderer.class);

    public void render(FractalImage canvas, AppConfig config, List<TransformationCoefficients> transformations) {
        int threads = config.threads();

        //AtomicLong totalIterationsCompleted = new AtomicLong(0);
        //AtomicInteger nextLogPercentage = new AtomicInteger(5);
//        try(ExecutorService service = Executors.newFixedThreadPool(threads);
//        ) {
//            long totalIterations = config.iterationCount();
//            long iterationsPerThread = config.iterationCount() / threads;
//
//            for (int i = 0; i < threads; ++i) {
//                long count = (i == threads - 1) ?
//                    config.iterationCount() - (iterationsPerThread * (threads - 1)):
//                    iterationsPerThread;
//                service.submit(() -> renderTask(canvas, config, count, transformations, totalIterationsCompleted, nextLogPercentage, totalIterations)
//                );
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Rendering failed", e);
//        }
        ExecutorService executor = Executors.newFixedThreadPool(threads);


        AtomicLong totalProgress = new AtomicLong(0);
        AtomicInteger nextLogThreshold = new AtomicInteger(5);
        long totalIterations = config.iterationCount();
        long iterationsPerThread = totalIterations / threads;

        List<Callable<FractalImage>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            long taskIterations = (i == threads - 1)
                ? totalIterations - (iterationsPerThread * i)
                : iterationsPerThread;

            tasks.add(() -> {

                FractalImage localCanvas = FractalImage.create(config.size().width(), config.size().height());
                renderTask(localCanvas, config, taskIterations, transformations, totalProgress, nextLogThreshold, totalIterations);
                return localCanvas;
            });
        }

        try {

            List<Future<FractalImage>> results = executor.invokeAll(tasks);


            LOGGER.info("Merging thread results...");
            for (Future<FractalImage> future : results) {
                //FractalImage threadImage = future.get();
                //mergeImages(canvas, threadImage);
                canvas.merge(future.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Rendering interrupted or failed", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

    }

    private void renderTask(FractalImage canvas, AppConfig config, long taskIterations,
                            List<TransformationCoefficients> transformations,
                            AtomicLong totalProgress, AtomicInteger nextLogThreshold, long globalTotalIterations) {


        List<PreparedVariation> preparedVariations = new ArrayList<>();
        for (TransformationFunction tf : config.functions()) {
            preparedVariations.add(new PreparedVariation(
                tf.weight(),
                VariationRegistry.get(tf.name())
            ));
        }

        var random = ThreadLocalRandom.current();
        double totalWeight = config.functions().stream().mapToDouble(TransformationFunction::weight).sum();

        Point currentPoint = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));


        for (int step = -20; step < 0; ++step) {
            currentPoint = applyTransformations(currentPoint, transformations, preparedVariations, random, totalWeight);
        }

        long iterationsPerUpdate = taskIterations / 5;
        if (iterationsPerUpdate == 0) iterationsPerUpdate = taskIterations;
        long localCounter = 0;


        for (int i = 0; i < taskIterations; ++i) {
            TransformationCoefficients coeff = transformations.get(random.nextInt(transformations.size()));

            currentPoint = coeff.affineTransformation().apply(currentPoint);
            currentPoint = applyVariations(currentPoint, preparedVariations, random, totalWeight);

            applySymmetryAndMap(canvas, currentPoint, coeff.color(), config);

            localCounter++;
            if (localCounter >= iterationsPerUpdate) {
                long currentTotal = totalProgress.addAndGet(localCounter);
                checkAndLogProgress(currentTotal, globalTotalIterations, nextLogThreshold);
                localCounter = 0;
            }
        }

        if (localCounter > 0) {
            long currentTotal = totalProgress.addAndGet(localCounter);
            checkAndLogProgress(currentTotal, globalTotalIterations, nextLogThreshold);
        }
    }

    private void checkAndLogProgress(long current, long total, AtomicInteger nextThreshold) {
        double percent = (double) current / total * 100.0;
        int threshold = nextThreshold.get();
        if (percent >= threshold) {
            if (nextThreshold.compareAndSet(threshold, threshold + 5)) {
                LOGGER.info(String.format("Progress: %d%% (%d/%d iterations)", (int)percent, current, total));
            }
        }
    }

    private Point applyVariations(Point p, List<PreparedVariation> variations, Random random, double totalWeight) {
        double r = random.nextDouble() * totalWeight;
        double currentSum = 0;


        for (PreparedVariation pv : variations) {
            currentSum += pv.weight();
            if (r <= currentSum) {
                return pv.function().apply(p); // Вызов лямбды напрямую! Без HashMap.get()
            }
        }
        return p;
    }

    private void mapPointToCanvasConcurrent(FractalImage canvas, Point p, Color trafoColor, AppConfig config) {
        double xMin = -1.77, xMax = 1.77;
        double yMin = -1.0, yMax = 1.0;

        if (p.x() >= xMin && p.x() <= xMax &&
            p.y() >= yMin && p.y() <= yMax) {

            int x = (int) ((p.x() - xMin) / (xMax - xMin) * canvas.width());
            int y = (int) ((p.y() - yMin) / (yMax - yMin) * canvas.height());

            if (canvas.contains(x, y)) {
                //Pixel pixel = canvas.pixel(x, y);

                //pixel.mixColor(trafoColor.getRed(), trafoColor.getGreen(), trafoColor.getBlue());
                //pixel.mixColorUnsafe(trafoColor.getRed(), trafoColor.getGreen(), trafoColor.getBlue());
                canvas.add(x, y, trafoColor.getRed(), trafoColor.getGreen(), trafoColor.getBlue());
            }
        }
    }

    private Point rotate(Point p, double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Point(
            p.x() * cos - p.y() * sin,
            p.x() * sin + p.y() * cos
        );
    }

    private Point applyTransformations(Point p, List<TransformationCoefficients> transformations,
                                       List<PreparedVariation> variations, // <-- Изменился тип
                                       Random random, double totalWeight) {
        TransformationCoefficients coeff = transformations.get(random.nextInt(transformations.size()));
        Point affinePoint = coeff.affineTransformation().apply(p);
        return applyVariations(affinePoint, variations, random, totalWeight);
    }

    private void applySymmetryAndMap(FractalImage canvas, Point p, Color color, AppConfig config) {
        for (int sym = 0; sym < config.symmetry(); ++sym) {
            double theta = 2 * Math.PI * sym / config.symmetry();
            Point rotatedPoint = rotate(p, theta);
            mapPointToCanvasConcurrent(canvas, rotatedPoint, color, config);
        }
    }


    private record PreparedVariation(double weight, Variation function) {}
//    private void mergeImages(FractalImage target, FractalImage source) {
//        for (int y = 0; y < target.size().height(); y++) {
//            for (int x = 0; x < target.size().width(); x++) {
//                Pixel sourcePixel = source.pixel(x, y);
//                if (sourcePixel.getHitCount() > 0) {
//                    Pixel targetPixel = target.pixel(x, y);
//
//                    // Убираем synchronized(targetPixel) - здесь нет гонки потоков!
//                    // Мы в главном потоке сливаем результаты по очереди.
//                    targetPixel.addHit(sourcePixel);
//                }
//            }
//        }
//    }
}
