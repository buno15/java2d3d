package src;

public class Vector {
    public float x;
    public float y;
    public float z;
    public float w;
    public Vector normal;

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

    public void initWeight() {
        w = 1.0f;
    }

    public void setWeight(float weight) {
        w = weight;
    }

    public void setNormal(Vector normal) {
        this.normal = normal;
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

    public float get(int axis) {
        switch (axis) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }

    // vector + vector
    public Vector add(Vector other) {
        float nx = x + other.x;
        float ny = y + other.y;
        float nz = z + other.z;

        return new Vector(nx, ny, nz, 1.0f);
    }
}
