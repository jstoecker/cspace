package cspace.render2d;

import java.awt.Font;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import jgl.core.Viewport;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3f;
import jgl.math.vector.Vector;

import com.jogamp.opengl.util.awt.TextRenderer;

import cspace.scene.Scene;
import cspace.scene.Sub;
import cspace.scene.trimesh.Sample;
import cspace.scene.trimesh.SampledSub;
import cspace.scene.trimesh.SampledSub.Triangle;
import cspace.scene.trimesh.SampledSub.Vertex;
import cspace.scene.visuals.RobotVisuals;

public class Renderer2D {

  Camera camera = new Camera();
  Scene scene;
  Viewport viewport;
  RobotView robotView;
  ObstacleView obstacleView;
  SubView subView;
  PathView pathView;
  SumEEView seeView;
  
  TextRenderer tr = new TextRenderer(new Font("Arial", Font.PLAIN, 12));

  public Renderer2D(Scene scene) {
    this.scene = scene;
    robotView = new RobotView(scene.cspace.robot, scene.visuals.robotVisuals);
    obstacleView = new ObstacleView(scene.cspace.obstacle,
            scene.visuals.obstacleVisuals);
    subView = new SubView(scene.cspace.subs, scene.visuals.subVisuals,
            scene.visuals.robotVisuals);
    pathView = new PathView(scene.path, scene.visuals.pathVisuals);
    seeView = new SumEEView(scene.cspace.sees, scene.visuals.sumEEVisuals,
            scene.visuals.robotVisuals);
    
    camera.setScale(scene.visuals.cameraVisuals.getScale2d());
    camera.setCenter(scene.visuals.cameraVisuals.getCenter2d());
  }

  public Viewport getViewport() {
    return viewport;
  }
  
  public Camera getCamera() {
    return camera;
  }
  
  public void display(GL2 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    camera.apply(gl, viewport);

    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

    obstacleView.draw(gl);
    seeView.draw(gl);
    subView.draw(gl);
    pathView.draw(gl);
    robotView.draw(gl);

    gl.glDisable(GL.GL_BLEND);
    
    // for visualizing new path being created:
    if (scene.newPath != null) {
      if (scene.newPath.waypoints.length > 0) {
        RobotVisuals vis = new RobotVisuals();
        vis.setColor(new Vec3f(0,1,0));
        vis.setP(scene.newPath.waypoints[0].p);
        vis.setU(scene.newPath.waypoints[0].u);
        RobotView view = new RobotView(scene.cspace.robot, vis);
        view.draw(gl);
      }
      if (scene.newPath.waypoints.length > 1) {
        RobotVisuals vis = new RobotVisuals();
        vis.setColor(new Vec3f(1,1,0));
        vis.setP(scene.newPath.waypoints[1].p);
        vis.setU(scene.newPath.waypoints[1].u);
        RobotView view = new RobotView(scene.cspace.robot, vis);
        view.draw(gl);
      }
    }

    SampledSub ssub = cspace.render3d.SubView.selected;
    if (ssub == null) {
      return;
    }
    Sub sub = ssub.sub;

    gl.glColor3f(0.5f, 0.5f, 0.5f);
    for (Triangle t : ssub.triangles) {
      Vec2d a = ssub.vertMap.get(t.sa).alphaTheta;
      Vec2d b = ssub.vertMap.get(t.sb).alphaTheta;
      Vec2d c = ssub.vertMap.get(t.sc).alphaTheta;
      gl.glBegin(GL2.GL_LINE_LOOP);
      gl.glVertex2d(a.x, a.y);
      gl.glVertex2d(b.x, b.y);
      gl.glVertex2d(c.x, c.y);
      gl.glEnd();
    }

    gl.glPointSize(8);

    int blah = ssub.outerLoop.size();
    int k = 0;
    gl.glBegin(GL2.GL_POINTS);
    gl.glColor3f(0, 0, 1);
    for (Vertex v : ssub.verts)
      gl.glVertex2d(v.alphaTheta.x, v.alphaTheta.y);
    int si = 0;
    int sj = ssub.startSamples.size() - 1;
    int sk = sj + ssub.tailSamples.size() - 1;
    int sl = sk + ssub.endSamples.size() - 1;
    for (Sample s : ssub.outerLoop) {
      
      boolean onStart = (si <= sj || si == ssub.outerLoop.size() - 1);
      boolean onTail = (si >= sj && si <= sk);
      boolean onEnd = (si >= sk && si < sl);
      boolean onHead = (si >= sl || si == 0);
      
      Vertex v = ssub.vertMap.get(s);

      gl.glColor3f(0.5f, 0.5f, 0.5f);
     if (onTail) {
       gl.glColor3f(1,0,0);
     }
     if (onHead) {
       gl.glColor3f(0, 1, 0);
     }
     
       Vec3f color = Vector.lerp(Vec3f.axisX(), Vec3f.axisY(), (float)(si+1)/ssub.outerLoop.size());
       gl.glColor3f(color.x, color.y, color.z);
      
      gl.glVertex2d(v.alphaTheta.x, v.alphaTheta.y);
      si++;
    }
    gl.glEnd();
//
//    tr.setColor(Color.white);
//    tr.begin3DRendering();
//    for (Vertex v : ssub.verts) {
//      tr.draw3D(""+v.index, (float)v.alphaTheta.x, (float)v.alphaTheta.y, 0, 0.001f);
//    }
//    tr.endRendering();
    

//    gl.glBegin(GL2.GL_POINTS);
//    gl.glColor3f(1, 0, 0);
//    for (int i = 0; i < ssub.outerLoop.size(); i++) {
//      Vec3f c1 = new Vec3f(1, 0, 0);
//      Vec3f c2 = new Vec3f(1, 0, 1);
//      Vec3f c = Vec3f.lerp(c1, c2, (float) i / (ssub.outerLoop.size() - 1));
//      gl.glColor3f(c.x, c.y, c.z);
//      drawIt(gl, ssub.outerLoop.get(i), sub);
//    }
//
//    gl.glColor3f(1, 1, 0);
//    for (List<Sample> sl : ssub.innerSamples) {
//      for (Sample s : sl) {
//        drawIt(gl, s, sub);
//      }
//    }
//    gl.glPointSize(1);

    gl.glEnd();

    gl.glColor3f(1, 0, 0);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex2d(-Math.PI * 2, -0.5);
    gl.glVertex2d(-Math.PI * 2, 0.5);
    gl.glVertex2d(-Math.PI, -0.5);
    gl.glVertex2d(-Math.PI, 0.5);
    gl.glVertex2d(Math.PI, -0.5);
    gl.glVertex2d(Math.PI, 0.5);
    gl.glVertex2d(Math.PI * 2, -0.5);
    gl.glVertex2d(Math.PI * 2, 0.5);
    gl.glEnd();

  }

  public void reshape(int x, int y, int w, int h) {
    viewport = new Viewport(x, y, w, h);
  }

  public void dispose(GL2 gl) {
    robotView.delete(gl);
    subView.delete(gl);
    obstacleView.delete(gl);
    pathView.delete(gl);
  }

  public void init(GL2 gl) {
  }
}
