package com.alxgrk.myownadventcalendar.measuring;

import java.util.Comparator;

/**
 * Created by alex on 14.11.16.
 */

public class Proportion {
    private final float width;

    private final float height;

    private final float proportion;

    public Proportion(float width, float height) {
        this.width = width;
        this.height = height;
        this.proportion = width / height;
    }

    public Proportion(int width, int height) {
        this((float) width, (float) height);
    }

    public float getProportion() {
        return proportion;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean withinBoundsOf(Proportion anotherProp) {
        return null == anotherProp || (this.getWidth() >= anotherProp.getWidth() || this.getHeight() >= anotherProp.getHeight());
    }

    @Override
    public String toString() {
        return "Proportion{" +
                "width=" + width +
                ", height=" + height +
                ", proportion=" + proportion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proportion that = (Proportion) o;

        if (Float.compare(that.width, width) != 0) return false;
        if (Float.compare(that.height, height) != 0) return false;
        return Float.compare(that.proportion, proportion) == 0;

    }

    @Override
    public int hashCode() {
        int result = (width != +0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != +0.0f ? Float.floatToIntBits(height) : 0);
        result = 31 * result + (proportion != +0.0f ? Float.floatToIntBits(proportion) : 0);
        return result;
    }
}
