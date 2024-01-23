package src;

public class Vector {
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        w = 1.0f;
    }

    public Vector(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void clearWeight() {
        w = 1.0f;
    }

    public void setWeight(float weight) {
        w = weight;
    }

    public float dot(Vector target) {
        return x * target.x + y * target.y + z * target.z + w * target.w;
    }

    public Vector cross(Vector other) {
        return new Vector(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x,
                1.0f);
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length != 0) {
            x /= length;
            y /= length;
            z /= length;
        }
    }

    public float distanceTo(Vector other) {
        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
