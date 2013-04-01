package cspace.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cspace.sampling.SampledCSpace;
import cspace.sampling.SampledSub;
import cspace.sampling.SampledSub.Triangle;
import cspace.sampling.SampledSub.Vertex;

/**
 * Writes a cspace mesh to OBJ format.
 */
public class OBJExporter {

  SampledCSpace cs;

  public OBJExporter(SampledCSpace cs) {
    this.cs = cs;
  }

  public void write(File file) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      int numVerts = 0;
      int numTriangles = 0;
      for (SampledSub ssub : cs.subSamplings.values()) {
        for (Vertex v : ssub.verts) {
          numVerts++;
        }
        for (Triangle t : ssub.triangles) {
          numTriangles++;
        }
      }
      
      out.write(String.format("# verts: %d\n", numVerts));
      out.write(String.format("# faces: %d\n", numTriangles));
      
      writeVerts(out);
      writeNormals(out);
      writeFaces(out);

      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeVerts(BufferedWriter out) throws IOException {
    for (SampledSub ssub : cs.subSamplings.values()) {
      for (Vertex v : ssub.verts) {
        out.write(String.format("v %f %f %f\n", v.position.x, v.position.y,
            v.position.z));
      }
    }
  }

  private void writeNormals(BufferedWriter out) throws IOException {
    for (SampledSub ssub : cs.subSamplings.values()) {
      for (Vertex v : ssub.verts) {
        out.write(String.format("vn %f %f %f\n", v.normal.x, v.normal.y,
            v.normal.z));
      }
    }
  }

  private void writeFaces(BufferedWriter out) throws IOException {
    int offset = 0;
    for (SampledSub ssub : cs.subSamplings.values()) {
      for (Triangle triangle : ssub.triangles) {
        int i = triangle.a.index + offset + 1;
        int j = triangle.b.index + offset + 1;
        int k = triangle.c.index + offset + 1;
        out.write(String.format("f %d//%d %d//%d %d//%d\n", i, i, j, j, k, k));
      }
      offset += ssub.verts.size();
    }
  }
}
