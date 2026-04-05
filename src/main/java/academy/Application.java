package academy;

import academy.config.AppConfig;
import academy.config.AffineParamsConverter;
import academy.config.TransformationFunctionsConverter;
import academy.config.dto.AffineParam;
import academy.config.dto.Size;
import academy.config.dto.TransformationFunction;
import academy.math.AffineTransformation;
import academy.math.TransformationCoefficients;
import academy.math.TransformationFactory;
import academy.model.FractalImage;
import academy.render.LogGammaProcessor;
import academy.render.MultiThreadRenderer;
import academy.utils.ImageUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "fractal-flame", version = "Example 1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ObjectReader YAML_READER =
            new ObjectMapper(new YAMLFactory()).findAndRegisterModules().reader();

    @Option(names = {"-w", "-width"})
    private Integer width;

    @Option(names = {"-h", "-height"})
    private Integer height;

    @Option(names = {"-i", "--iteration-count"})
    private Integer iterations;

    @Option(names = {"-o", "--output-path"})
    private String outputPath;

    @Option(names = {"-t", "--threads"})
    private Integer threads;

    @Option(names = {"--seed"})
    private Double seed;

    @Option(names = {"-ap", "--affine-params"}, converter = AffineParamsConverter.class)
    private List<AffineParam> cliAffineParams;

    @Option(names = {"-f", "--functions"}, converter = TransformationFunctionsConverter.class)
    private List<TransformationFunction> cliFunctions;

    @Option(names = "--config")
    private File configFile;

    @Option(names = {"-s", "--symmetry-level"}, defaultValue = "1", description = "Number of symmetry axes")
    private Integer symmetry;

    @Option(names = {"-g", "--gamma-correction"}, description = "Gamma value")
    private Double gammaValue;

//    @Option(
//            names = {"-s", "--font-size"},
//            description = "Font size",
//            defaultValue = "12")
//    int fontSize;
//
//    @Parameters(
//            paramLabel = "<word>",
//            defaultValue = "Hello, picocli",
//            description = "Words to be translated into ASCII art.")
//    private String[] words;
//
//    @Option(
//            names = {"-c", "--config"},
//            description = "Path to JSON config file")
//    private File configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);

        if (System.getProperty("testMode") == null) {
            System.exit(exitCode);
        }
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        try {


            LOGGER.info("Starting fractal generation...");

//        List<TransformationCoefficients> transformations = List.of(
//            new TransformationCoefficients(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0), Color.RED),
//            new TransformationCoefficients(new AffineTransformation(0.5, 0, 1, 0, 0.5, 0), Color.GREEN),
//            new TransformationCoefficients(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 1), Color.BLUE)
//        );

//        AppConfig config = AppConfig.defaultConfiguration();

            LOGGER.info("Building config");

            AppConfig config = buildConfig();

            LOGGER.info("Preparing empty image");

            // Передаем ширину и высоту отдельно
            FractalImage canvas = FractalImage.create(config.size().width(), config.size().height());
            LOGGER.info("Preparing transformation functions");

            List<TransformationCoefficients> transformations = TransformationFactory.prepareTransformations(config);



//            SingleThreadedRenderer renderer = new SingleThreadedRenderer();
//            renderer.render(canvas, config, transformations);

            LOGGER.info("Rendering with affine and function transformation");

            MultiThreadRenderer renderer = new MultiThreadRenderer();
            renderer.render(canvas, config, transformations);

            LOGGER.info("Logarithmic brightness and gamma correction");

            LogGammaProcessor processor = new LogGammaProcessor();
            processor.process(canvas, config.gamma());

            LOGGER.info("Saving image to {}", config.outputPath());
            ImageUtils.save(canvas, Path.of(config.outputPath()));

        } catch (Exception e) {
            LOGGER.info("Error while execution: {}", e.getMessage());
            System.err.println("Error while execution: " + e.getMessage());
        } finally {
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            LOGGER.info("Total execution time: {} seconds", String.format("%.3f", durationSeconds));
            LOGGER.info("Application finished.");
        }

    }

    private AppConfig buildConfig() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.findAndRegisterModules();

        AppConfig config;

        if (configFile != null && configFile.exists()) {
            try {
                config = mapper.readValue(configFile, AppConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Error when reading the configuration file: " + e.getMessage());
            }
        } else {
            config = AppConfig.defaultConfiguration();
        }

        return mergeWithCli(config);
    }

    private AppConfig mergeWithCli(AppConfig base) {
        Size size = new Size(
          width != null ? width: base.size().width(),
          height != null ? height: base.size().height()
        );

        return new AppConfig(
            size,
            iterations != null ? iterations: base.iterationCount(),
            outputPath != null ? outputPath: base.outputPath(),
            threads != null ? threads: base.threads(),
            seed != null ? seed: base.seed(),
            cliFunctions != null ? cliFunctions: base.functions(),
            cliAffineParams != null ? cliAffineParams: base.affineParams(),
            symmetry != null ? symmetry: base.symmetry(),
            gammaValue != null ? gammaValue: base.gamma()
                );
    }

//    private List<TransformationCoefficients> prepareTransformations(AppConfig config) {
//        List<TransformationCoefficients> result = new ArrayList<>();
//        long longSeed = Double.doubleToRawLongBits(config.seed());
//        Random random = new Random(longSeed);
//
//        for (AffineParam p: config.affineParams()) {
//            AffineTransformation at = new AffineTransformation(p.a(), p.b(), p.c(), p.d(), p.e(), p.f());
//
//            Color color;
//            if (p.rgb() != null && p.rgb().length == 3) {
//                color = new Color(p.rgb()[0], p.rgb()[1], p.rgb()[2]);
//            } else {
//                color = new Color(random.nextInt(0xFF), random.nextInt(0xFF), random.nextInt(0xFF));
//            }
//            result.add(new TransformationCoefficients(at, color));
//        }
//
//        return result;
//    }

    private AppConfig loadJsonConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.findAndRegisterModules();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(file, AppConfig.class);
    }

//    private AppConfig loadConfig() {
//        // fill with cli options
//        if (configPath == null) return new AppConfig(fontSize, words);
//
//        // use config file if provided
//        try {
//            return YAML_READER.readValue(configPath, AppConfig.class);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
}
