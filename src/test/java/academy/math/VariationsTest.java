package academy.math;

import academy.model.Point;
import academy.render.Variation;
import academy.render.VariationRegistry;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VariationsTest {
    private static final double EPSILON = 1e-10;

    @Test
    @DisplayName("Should return correct variation by name")
    void testGetVariation() {
        Point p = new Point(Math.PI / 2, 0);

        Variation func = VariationRegistry.get("sinusoidal");
        Point result = func.apply(p);

        assertThat(result.x()).isCloseTo(1.0, Percentage.withPercentage(0.0001));
    }

    @Test
    @DisplayName("Should be case insensitive")
    void testCaseInsensitivity() {
        Variation v1 = VariationRegistry.get("Heart");
        Variation v2 = VariationRegistry.get("heart");

        assertThat(v1).isNotNull().isEqualTo(v2);
    }

    @Test
    @DisplayName("Should return Linear for unknown names or null")
    void testDefault() {
        Variation v1 = VariationRegistry.get("unknown_function");
        Variation v2 = VariationRegistry.get(null);

        Point p = new Point(10, 20);

        assertThat(v1.apply(p)).isEqualTo(p);
        assertThat(v2.apply(p)).isEqualTo(p);
    }

    @Test
    @DisplayName("Linear transformation should return the same point")
    void linearTransformationTest() {
        Point input = new Point(1.5, -2.0);
        Point result = Variations.linear().apply(input);

        assertAll(
            () -> assertEquals(1.5, result.x(), EPSILON),
            () -> assertEquals(-2.0, result.y(), EPSILON)
        );
    }

    @Test
    @DisplayName("Sinusoidal transformation calculation check")
    void sinusoidalTransformationTest() {
        Point input = new Point(Math.PI / 2, 0);
        Point result = Variations.sinusoidal().apply(input);

        assertAll(
            () -> assertEquals(1.0, result.x(), EPSILON),
            () -> assertEquals(0.0, result.y(), EPSILON)
            );

    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 0.0, 1.0, 0.0",
        "0.0, 2.0, 0.0, 0.5",
        "1.0, 1.0, 0.5, 0.5"
    })
    @DisplayName("Spherical transformation should scale points by 1/r^2")
    void sphericalTransformationTest(double inX, double inY, double epsX, double epsY) {
        Point result = Variations.spherical().apply(new Point(inX, inY));

        assertAll(
            () -> assertEquals(epsX, result.x(), EPSILON),
            () -> assertEquals(epsY, result.y(), EPSILON)
        );

    }

    @Test
    @DisplayName("Swirl transformation calculation check")
    void swirlTransformationTest() {
        double r = Math.sqrt(Math.PI / 2);
        Point input = new Point(r, 0);
        Point result = Variations.swirl().apply(input);

        assertAll(
            () -> assertEquals(r, result.x(), EPSILON),
            () -> assertEquals(0.0, result.y(), EPSILON)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 5.0, 5.0, 5.0, 5.0",
        "0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0"
    })
    @DisplayName("Affine transformation calculation check")
    void affineTransformationTest(double a, double b, double c,
                                  double d, double e, double f,
                                  double inX, double inY, double expX, double expY) {
        AffineTransformation affine = new AffineTransformation(a, b, c, d, e, f);
        Point result = affine.apply(new Point(inX, inY));

        assertAll(
            () -> assertEquals(expX, result.x(), EPSILON),
            () -> assertEquals(expY, result.y(), EPSILON)
            );

    }

    @Test
    @DisplayName("Heart transformation logic check")
    void heartTransformationTest() {
        Point input = new Point(1.0, 0.0);
        Point result = Variations.heart().apply(input);

        assertAll(
            () -> assertEquals(0.0, result.x(), EPSILON),
            () -> assertEquals(-1.0, result.y(), EPSILON)
        );
    }

    @Test
    @DisplayName("DiskTransformationTest")
    void diskTransformationTest() {
        Point input = new Point(1.0, 0.0);
        Point result = Variations.disk().apply(input);

        assertAll(
            () -> assertEquals(0.0, result.x(), EPSILON),
            () -> assertEquals(0.0, result.y(), EPSILON)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"linear", "sinusoidal", "spherical", "swirl", "heart", "disk", "horseshoe"})
    void allVariations_ShouldReturnValidPoints(String name) {
        Point input = new Point(0.5, 0.5);
        Variation variation = VariationRegistry.get(name);

        Point result = variation.apply(input);

        assertNotNull(result);
        assertFalse(Double.isNaN(result.x()));
        assertFalse(Double.isNaN(result.y()));
    }
}
