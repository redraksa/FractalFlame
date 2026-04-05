package academy.model;

import academy.config.dto.Size;

public record FractalImage(int width, int height, int[] red, int[] green, int[] blue, int[] hitCounts) {

    public static FractalImage create(int width, int height) {
        int size = width * height;
        return new FractalImage(
            width, height,
            new int[size], new int[size], new int[size], new int[size]
        );
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }


    public void add(int x, int y, int r, int g, int b) {
        int index = y * width + x;
        // Простая арифметика вместо AtomicInteger и Lock
        if (hitCounts[index] == 0) {
            red[index] = r;
            green[index] = g;
            blue[index] = b;
        } else {
            red[index] = (red[index] + r) / 2;
            green[index] = (green[index] + g) / 2;
            blue[index] = (blue[index] + b) / 2;
        }
        hitCounts[index]++;
    }

    /**
     * Слияние другого изображения в текущее.
     * Используется главным потоком для объединения результатов.
     */
    public void merge(FractalImage other) {
        for (int i = 0; i < hitCounts.length; i++) {
            if (other.hitCounts[i] == 0) continue;

            if (this.hitCounts[i] == 0) {
                this.red[i] = other.red[i];
                this.green[i] = other.green[i];
                this.blue[i] = other.blue[i];
            } else {
                this.red[i] = (this.red[i] + other.red[i]) / 2;
                this.green[i] = (this.green[i] + other.green[i]) / 2;
                this.blue[i] = (this.blue[i] + other.blue[i]) / 2;
            }
            this.hitCounts[i] += other.hitCounts[i];
        }
    }


    public Pixel pixel(int x, int y) {
        int index = y * width + x;
        return new Pixel(red[index], green[index], blue[index], hitCounts[index]);
    }
}

//public record FractalImage(Pixel[] data, Size size) {
//    public static FractalImage create(Size size) {
//        Pixel[] data = new Pixel[size.width() * size.height()];
//        for (int i = 0; i < data.length; ++i) {
//            data[i] = new Pixel(0, 0,0,0);
//        }
//        return new FractalImage(data, size);
//    }
//
//    public boolean contains(int x, int y) {
//        return x >= 0 && y >= 0 && x < size.width() && y < size.height();
//    }
//
//    public Pixel pixel(int x, int y) {
//        return data[y * size.width() + x];
//    }
//}
