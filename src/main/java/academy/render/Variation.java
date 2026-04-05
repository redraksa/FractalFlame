package academy.render;

import academy.model.Point;

@FunctionalInterface
public interface Variation {
    Point apply(Point p);
}
