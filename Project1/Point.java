package Project1;

public class Point {
    double x, y, z;
    double scalars;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    void setScalars(double scalars) {
        this.scalars = scalars;
    }

    void normalizedScalars(double minScalar, double maxScalar) {
        scalars = (scalars - minScalar) / (maxScalar - minScalar);
    }

    Point scaleAndTransformPoint(double scaleX, double scaleY, Point minPoint, Point maxPoint) {
        double transformedX = (x - minPoint.x) * scaleX;
        double transformedY = (maxPoint.y - y) * scaleY;

        Point np = new Point(transformedX, transformedY, 0);
        np.setScalars(scalars);
        return np;
    }
}
