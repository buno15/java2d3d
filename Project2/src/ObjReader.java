package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ObjReader {
    private ArrayList<Vector> vertices;
    private ArrayList<Vector> normals;
    private ArrayList<Face> faces;

    public ObjReader() {
        vertices = new ArrayList<Vector>();
        normals = new ArrayList<Vector>();
        faces = new ArrayList<Face>();
    }

    public MeshList readobj(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                // skip empty lines
                if (tokens.length <= 0)
                    continue;
                // skip the object name
                if (tokens[0].equals("o"))
                    continue;
                if (tokens[0].equals("v")) {
                    Vector v = new Vector(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]), 1.0f);
                    vertices.add(v);
                }
                // skip texture coordinates
                if (tokens[0].equals("vt"))
                    continue;
                if (tokens[0].equals("vn")) {
                    Vector n = new Vector(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]), 0.0f);
                    normals.add(n);
                }
                if (tokens[0].equals("f")) {
                    int numV = tokens.length;
                    Face f = new Face(numV - 1);

                    for (int i = 1; i < numV; i++) {
                        String[] faceTokens = tokens;
                        int v1 = Integer.parseInt(faceTokens[1]);
                        int v2 = Integer.parseInt(faceTokens[2]);
                        int v3 = Integer.parseInt(faceTokens[3]);

                        // index starts at 1 in .obj
                        f.addVertexIndex(v1 - 1);
                        f.addVertexIndex(v2 - 1);
                        f.addVertexIndex(v3 - 1);
                    }
                    faces.add(f);
                }
            }
            return new MeshList(vertices, normals, faces);
        } catch (Exception e) {
            System.out.println("Error in readobj");
            e.printStackTrace();
            return new MeshList();
        }
    }
}
