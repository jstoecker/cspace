package cspace.scene2d;

import javax.media.opengl.GL2;

import cspace.model.ArcShape;
import cspace.visuals.ObstacleVisuals;

public class ObstacleView extends ArcShapeView {

  ObstacleVisuals visuals;

  public ObstacleView(ArcShape obstacle, ObstacleVisuals visuals) {
    super(obstacle, visuals);
    this.visuals = visuals;
  }
  
  @Override
  public void draw(GL2 gl) {
    if (!visuals.isVisible2d())
      return;
    super.draw(gl);
  }
}
