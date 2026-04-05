package academy.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pixel {
    private int r, g, b;
    //private final AtomicInteger hitCount = new AtomicInteger(0);
    //private final Lock lock = new ReentrantLock();
    private int hitCount;

    public Pixel(int r, int g, int b, int hitCount) {
        this.r = r;
        this.g = g;
        this.b = b;
        //this.hitCount.set(hitCount);
        this.hitCount = hitCount;
    }

//    public void mixColor(int newR, int newG, int newB) {
//        lock.lock();
//        try {
//            if (hitCount.get() == 0) {
//                this.r = newR;
//                this.g = newG;
//                this.b = newB;
//            } else {
//                this.r = (this.r + newR) / 2;
//                this.g = (this.g + newG) / 2;
//                this.b = (this.b + newB) / 2;
//            }
//        } finally {
//            lock.unlock();
//        }
//
//        hitCount.incrementAndGet();
//    }
//
//    public void mixColorUnsafe(int newR, int newG, int newB) {
//        if (this.r == 0 && this.g == 0 && this.b == 0) { // Простая проверка вместо атомика
//            this.r = newR;
//            this.g = newG;
//            this.b = newB;
//        } else {
//            this.r = (this.r + newR) / 2;
//            this.g = (this.g + newG) / 2;
//            this.b = (this.b + newB) / 2;
//        }
//        hitCount.incrementAndGet();
//    }



    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

//    public int getHitCount() {
//        return hitCount.get();
//    }

    public int getHitCount() {
        return hitCount;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

//    public void setHitCount(int hitCount) {
//        this.hitCount = hitCount;
//    }

//    public void incrementHitCount() {
//        hitCount.incrementAndGet();
//    }
//
//    public void addHit(Pixel other) { // Убираем synchronized, если слияние однопоточное
//        int otherHits = other.getHitCount();
//        if (otherHits == 0) return;
//
//        // lock.lock(); // УБИРАЕМ
//        // try {
//        if (this.hitCount.get() == 0) {
//            this.r = other.getR();
//            this.g = other.getG();
//            this.b = other.getB();
//        } else {
//            this.r = (this.r + other.getR()) / 2;
//            this.g = (this.g + other.getG()) / 2;
//            this.b = (this.b + other.getB()) / 2;
//        }
//        this.hitCount.addAndGet(otherHits);
//        // } finally { lock.unlock(); } // УБИРАЕМ
//    }
}
