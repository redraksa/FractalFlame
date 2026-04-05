package academy.config;

import academy.config.dto.AffineParam;
import academy.config.dto.Size;
import academy.config.dto.TransformationFunction;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AppConfig(
    Size size,
    @JsonProperty("iteration_count") int iterationCount,
    @JsonProperty("output_path") String outputPath,
    int threads,
    double seed,
    List<TransformationFunction> functions,
    @JsonProperty("affine_params") List<AffineParam> affineParams,
    @JsonProperty("symmetry_level") int symmetry,
    double gamma
) {

    public static AppConfig defaultConfiguration() {
        return new AppConfig(
            new Size(1920, 1080),
            2500,
            "result.png",
            1,
            2.1324512,
            List.of(new TransformationFunction("linear", 1.0)),
            List.of(new AffineParam(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, null)),
            1,
            2.2
        );
    }
}
