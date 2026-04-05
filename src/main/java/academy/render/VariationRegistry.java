package academy.render;

import academy.math.Variations;
import academy.model.Point;
import java.util.Map;

public class VariationRegistry {
    private static final Map<String, Variation> VARIATION_MAP = Map.of(
        "linear", p -> p,
        "sinusoidal", p -> new Point(Math.sin(p.x()), Math.sin(p.y())),
        "spherical", p -> {
            double r2 = p.x() * p.x() + p.y() * p.y();
            return new Point(p.x() / r2, p.y() / r2);
        },
        "swirl", p -> {
            double r2 = p.x() * p.x() + p.y() * p.y();
            return new Point(p.x() * Math.sin(r2) - p.y() * Math.cos(r2),
                p.x() * Math.cos(r2) + p.y() * Math.sin(r2));
        },
        "heart", p -> {
            double r = Math.hypot(p.x(), p.y());
            double theta = Math.atan2(p.y(), p.x());
            return new Point(
                r * Math.sin(theta * r),
                -r * Math.cos(theta * r)
            );
        },
        "disk", p -> {
            double r = Math.hypot(p.x(), p.y());
            double theta = Math.atan2(p.y(), p.x());
            return new Point(
                (theta / Math.PI) * Math.sin(Math.PI * r),
                (theta / Math.PI) * Math.cos(Math.PI * r)
            );
        },
        "horseshoe", p -> {
            double r = Math.hypot(p.x(), p.y());
            if (r == 0) {
                return p;
            }
            return new Point(
                ((p.x() - p.y()) * (p.x() + p.y())) / r,
                (2.0 * p.x() * p.y()) / r
            );
        }
    );

    public static Variation get(String name) {
        if (name == null) return VARIATION_MAP.get("linear");
        return VARIATION_MAP.getOrDefault(name.toLowerCase(), VARIATION_MAP.get("linear"));
    }
}

