package academy.config.converter;

import academy.config.AffineParamsConverter;
import academy.config.TransformationFunctionsConverter;
import academy.config.dto.AffineParam;
import academy.config.dto.TransformationFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConverterTest {
    @Test
    @DisplayName("AffineParamsConverter should parse valid string")
    void testAffineConverter() throws Exception {
        AffineParamsConverter converter = new AffineParamsConverter();

        String input = "0.5,0,0,0,0.5,0/1,0,0,0,1,0";

        List<AffineParam> result = converter.convert(input);

        assertEquals(2, result.size());
        assertEquals(0.5, result.get(0).a());
        assertEquals(1.0, result.get(1).a());
        assertNull(result.get(0).rgb());
    }

    @Test
    @DisplayName("AffineParamsConverter should throw on invalid input")
    void testAffineConverterError() {
        AffineParamsConverter converter = new AffineParamsConverter();
        String input = "0.5,0";

        assertThrows(IllegalArgumentException.class, () -> converter.convert(input));
    }

    @Test
    @DisplayName("TransformationFunctionsConverter should parse valid string")
    void testFunctionConverter() throws Exception {
        TransformationFunctionsConverter converter = new TransformationFunctionsConverter();
        String input = "swirl:1.0, heart:0.5";

        List<TransformationFunction> result = converter.convert(input);

        assertEquals(2, result.size());
        assertEquals("swirl", result.get(0).name());
        assertEquals(1.0, result.get(0).weight());
        assertEquals("heart", result.get(1).name());
        assertEquals(0.5, result.get(1).weight());
    }
}
