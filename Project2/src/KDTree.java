package src;

public class KDTree {
    private static final int K = 3; // 3次元空間
    private Node root;

    private static class Node {
        Vector point;
        Node left, right;
        int axis;

        public Node(Vector point, int axis) {
            this.point = point;
            this.axis = axis;
        }
    }

    public KDTree() {
        root = null;
    }

    public void insert(Vector point) {
        root = insertRec(root, point, 0);
    }

    private Node insertRec(Node root, Vector point, int depth) {
        if (root == null) {
            return new Node(point, depth % K);
        }

        int cd = root.axis;
        if (point.get(cd) < root.point.get(cd)) {
            root.left = insertRec(root.left, point, depth + 1);
        } else {
            root.right = insertRec(root.right, point, depth + 1);
        }

        return root;
    }

}