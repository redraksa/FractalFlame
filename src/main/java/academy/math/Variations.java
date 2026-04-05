package academy.math;

import academy.model.Point;

public class Variations {

    public static Transformation linear() {
        return p -> p;
    }

    public static Transformation sinusoidal() {
        return p -> new Point(Math.sin(p.x()), Math.sin(p.y()));
    }

    public static Transformation spherical() {
        return p -> {
            double r2 = p.x() * p.x() + p.y() * p.y();
            return new Point(p.x() / r2, p.y() / r2);
        };
    }

    public static Transformation swirl() {
        return p -> {
            double r2 = p.x() * p.x() + p.y() * p.y();
            return new Point(p.x() * Math.sin(r2) - p.y() * Math.cos(r2),
                p.x() * Math.cos(r2) + p.y() * Math.sin(r2));
        };
    }

    public static Transformation heart() {
        return p -> {
            double r = Math.hypot(p.x(), p.y());
            double theta = Math.atan2(p.y(), p.x());
            return new Point(
                r * Math.sin(theta * r),
                -r * Math.cos(theta * r)
            );
        };
    }

    public static Transformation disk() {
        return p -> {
            double r = Math.hypot(p.x(), p.y());
            double theta = Math.atan2(p.y(), p.x());
            return new Point(
                (theta / Math.PI) * Math.sin(Math.PI * r),
                (theta / Math.PI) * Math.cos(Math.PI * r)
                );
        };
    }
}
