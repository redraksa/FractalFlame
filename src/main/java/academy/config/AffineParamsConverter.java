package academy.config;

import academy.config.dto.AffineParam;
import picocli.CommandLine;
import java.util.ArrayList;
import java.util.List;

public class AffineParamsConverter implements CommandLine.ITypeConverter<List<AffineParam>> {
    @Override
    public List<AffineParam> convert(String s) throws Exception {
        List<AffineParam> params = new ArrayList<>();

        String[] sets = s.split("/");
        for (String set: sets) {
            String[] parts = set.split(",");
            if (parts.length != 6) {
                throw new IllegalArgumentException("Affine set must have 6 parts (a, b, c, d, e, f).");
            }

            params.add(new AffineParam(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]),
                Double.parseDouble(parts[5]),
                null
            ));
        }
        return params;
    }
}
