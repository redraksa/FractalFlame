package academy.render;

import academy.config.AppConfig;
import academy.config.dto.AffineParam;
import academy.config.dto.Size;
import academy.config.dto.TransformationFunction;
import academy.math.TransformationCoefficients;
import academy.math.TransformationFactory;
import academy.model.FractalImage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Tag("benchmark")
public class RenderBenchmarkTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RenderBenchmarkTest.class);

    @Test
    void compareThreadPerformance() {
        int iterations = 10_000_000;
        AppConfig baseConfig = AppConfig.defaultConfiguration();

        List<TransformationCoefficients> transformations = TransformationFactory.prepareTransformations(baseConfig);

        MultiThreadRenderer renderer = new MultiThreadRenderer();

        int[] threadCounts = {1, 2, 4, 8};

        LOGGER.info("Warming up JVM...");
        AppConfig warmupConfig = createTestConfig(baseConfig, 1, 50_000_000);
        renderer.render(FractalImage.create(warmupConfig.size().width(), warmupConfig.size().height()), warmupConfig, transformations);
        LOGGER.info("Warm-up finished.");

        LOGGER.info("--- Performance Benchmark (Iterations: {}) ---", iterations);
        LOGGER.info(String.format("%-10s | %-15s | %-10s", "Threads", "Time (ms)", "Speedup"));
        LOGGER.info("----------------------------------------------------------");
        long baselineTime = 0;

        for (int t: threadCounts) {
            AppConfig testConfig = createTestConfig(baseConfig, t, iterations);
            FractalImage canvas = FractalImage.create(testConfig.size().width(), testConfig.size().height());

            long start = System.nanoTime();
            renderer.render(canvas, testConfig, transformations);
            long end = System.nanoTime();

            long durationNs = end - start;
            long durationMs = durationNs / 1_000_000;

            if (t == 1) baselineTime = durationNs;

            double speedup = (double) baselineTime / durationNs;

            LOGGER.info(String.format("%-10d | %-15d | %.2fx", t, durationMs, speedup));

            System.gc();
        }
    }

    private AppConfig createTestConfig(AppConfig base, int threads, int iterations) {
        return new AppConfig(
            base.size(),
            iterations,
            base.outputPath(),
            threads,
            base.seed(),
            List.of(
                new TransformationFunction("sinusoidal", 0.25),
                new TransformationFunction("spherical", 0.25),
                new TransformationFunction("swirl", 0.25),
                new TransformationFunction("heart", 0.25)
            ),
            base.affineParams(),
            base.symmetry(),
            base.gamma()
        );
    }
}
