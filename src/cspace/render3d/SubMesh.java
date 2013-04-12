package cspace.render3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.GLBuffer;

import com.jogamp.common.nio.Buffers;

import cspace.scene.EdgePair;
import cspace.scene.Scene;
import cspace.scene.SceneView.Subs.ColorStyle3D;
import cspace.scene.triangulate.SampledCSpace;
import cspace.scene.triangulate.SampledSub;
import cspace.scene.triangulate.SampledSub.Triangle;
import cspace.scene.triangulate.SampledSub.Vertex;

public class SubMesh {

  private Scene     scene;
  private GLBuffer  vertexBuf      = new GLBuffer(GLBuffer.Target.ARRAY, GLBuffer.Usage.STATIC_DRAW);
  private GLBuffer  indexBuf       = new GLBuffer(GLBuffer.Target.ELEMENT_ARRAY,
                                       GLBuffer.Usage.STATIC_DRAW);
  private int       numTriangles;

  private final int typeSize       = Float.SIZE / 8;
  // x, y, z, nx, ny, nz, r, g, b, sumR, sumG, sumB
  private final int stride         = typeSize * 12;
  private final int vertexOffset   = 0;
  private final int normalOffset   = vertexOffset + typeSize * 3;

  // used when coloring per sub
  private final int colorOffset    = normalOffset + typeSize * 3;
  // used when coloring per sum
  private final int colorOffsetSum = colorOffset + typeSize * 3;

  // temporary buffers that store vertex & index data to be uploaded:
  FloatBuffer       vData;
  IntBuffer         iData;

  public SubMesh(Scene scene) {
    this.scene = scene;
  }

  void update(Scene scene) {
    int numVerts = 0;
    numTriangles = 0;

    SampledCSpace sampling = scene.sampledCS;

    for (SampledSub sub : sampling.subSamplings.values()) {
      numVerts += sub.verts.size();
      numTriangles += sub.triangles.size();
    }

    int offset = 0;
    vData = Buffers.newDirectFloatBuffer(numVerts * stride / typeSize);
    iData = Buffers.newDirectIntBuffer(numTriangles * 3);
    for (SampledSub ssub : sampling.subSamplings.values()) {
      for (Vertex v : ssub.verts) {
        v.position.toFloat().putInto(vData);
        v.normal.toFloat().putInto(vData);
        scene.view.subs.getColor(ssub.sub).putInto(vData);
        scene.view.sums.getColor(new EdgePair(ssub.sub.robEdge.index, ssub.sub.obsEdge.index)).putInto(vData);
      }
      for (Triangle triangle : ssub.triangles) {
        iData.put(triangle.a.index + offset);
        iData.put(triangle.b.index + offset);
        iData.put(triangle.c.index + offset);
      }
      offset += ssub.verts.size();
    }
    vData.rewind();
    iData.rewind();
  }

  void delete(GL gl) {
    vertexBuf.delete(gl);
    indexBuf.delete(gl);
  }

  void setState(GL2 gl) {
    if (vData != null && iData != null) {
      vertexBuf.bind(gl);
      vertexBuf.setData(gl, null);
      vertexBuf.setData(gl, vData);
      vData = null;

      indexBuf.bind(gl);
      indexBuf.setData(gl, null);
      indexBuf.setData(gl, iData);
      iData = null;
    } else {
      vertexBuf.bind(gl);
      indexBuf.bind(gl);
    }

    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glVertexPointer(3, GL2.GL_FLOAT, stride, vertexOffset);

    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glNormalPointer(GL2.GL_FLOAT, stride, normalOffset);

    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
    if (scene.view.subs.colorStyle3d == ColorStyle3D.PER_SUM)
      gl.glColorPointer(3, GL2.GL_FLOAT, stride, colorOffsetSum);
    else
      gl.glColorPointer(3, GL2.GL_FLOAT, stride, colorOffset);
  }

  void unsetState(GL2 gl) {
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glVertexPointer(3, GL2.GL_FLOAT, stride, vertexOffset);
    gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glNormalPointer(GL2.GL_FLOAT, stride, normalOffset);
    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
    vertexBuf.unbind(gl);
    indexBuf.unbind(gl);
  }

  void draw(GL2 gl) {
    gl.glDrawElements(GL.GL_TRIANGLES, numTriangles * 3, GL.GL_UNSIGNED_INT, 0);
  }
}