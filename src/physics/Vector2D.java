package physics;

public class Vector2D {

    public double x;
    public double y;


    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public Vector2D() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public void add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void sub(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void mult(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }


    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double m = mag();
        if (m != 0) {
            this.x /= m;
            this.y /= m;
        }
    }

    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }
}
