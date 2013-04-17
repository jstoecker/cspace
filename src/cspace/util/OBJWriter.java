package cspace.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cspace.scene.CSpace;
import cspace.scene.Sub;

/**
 * Writes a triangulated cspace to OBJ format.
 */
public class OBJWriter {

  private CSpace cspace;

  public OBJWriter(CSpace cs) {
    this.cspace = cs;
  }

  public void write(File file) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      // write output size (meta data)
      int numVerts = 0;
      int numTriangles = 0;
      for (Sub sub : cspace.subs) {
        numVerts += sub.verts.size();
        numTriangles += sub.triangles.size();
      }
      out.write(String.format("# verts: %d\n", numVerts));
      out.write(String.format("# faces: %d\n", numTriangles));
      
      // write vertex positions
      for (Sub sub : cspace.subs)
        for (Sub.Vertex v : sub.verts)
          out.write(String.format("v %f %f %f\n", v.position.x, v.position.y, v.position.z));
      
      // write vertex normals
      for (Sub sub : cspace.subs)
        for (Sub.Vertex v : sub.verts)
          out.write(String.format("vn %f %f %f\n", v.normal.x, v.normal.y, v.normal.z));
      
      // write face indices
      int offset = 0;
      for (Sub sub : cspace.subs) {
        for (Sub.Triangle triangle : sub.triangles) {
          int i = triangle.a.index + offset + 1;
          int j = triangle.b.index + offset + 1;
          int k = triangle.c.index + offset + 1;
          out.write(String.format("f %d//%d %d//%d %d//%d\n", i, i, j, j, k, k));
        }
        offset += sub.verts.size();
      }

      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
