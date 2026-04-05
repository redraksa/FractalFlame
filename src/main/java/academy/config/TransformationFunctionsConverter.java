package academy.config;

import academy.config.dto.TransformationFunction;
import picocli.CommandLine;
import java.util.ArrayList;
import java.util.List;

public class TransformationFunctionsConverter implements CommandLine.ITypeConverter<List<TransformationFunction>> {
    @Override
    public List<TransformationFunction> convert(String s) throws Exception {
        List<TransformationFunction> functions = new ArrayList<>();
        String[] parts = s.split(",");

        for (String part : parts) {
            String[] funcParts = part.split(":");
            if (funcParts.length != 2) {
                throw new IllegalArgumentException("Invalid function format: " + part + ". Expected name: weight");
            }
            String name = funcParts[0].trim();
            double weight = Double.parseDouble(funcParts[1].trim());
            functions.add(new TransformationFunction(name, weight));
        }

        return functions;
    }
}
