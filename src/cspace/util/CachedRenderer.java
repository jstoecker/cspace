package cspace.util;

import javax.media.opengl.GL2;

/**
 * Renderer that uses a display list to cache draw calls.
 * 
 * @author justin
 */
public abstract class CachedRenderer {
  
  private int     list  = -1;
  private boolean dirty = true;

  public void markDirty() {
    dirty = true;
  }

  public void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 1);
      list = -1;
    }
  }

  public final void draw(GL2 gl) {
    if (!isVisible())
      return;
    if (dirty)
      update(gl);
    beginDraw(gl);
    gl.glCallList(list);
    endDraw(gl);
  }

  private final void update(GL2 gl) {
    delete(gl);
    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    updateGeometry(gl);
    gl.glEndList();
    dirty = false;
  }

  /** Called before the display list */
  protected void beginDraw(GL2 gl) {
  }

  /** Called after the display list */
  protected void endDraw(GL2 gl) {
  }

  protected abstract boolean isVisible();
  
  protected abstract void updateGeometry(GL2 gl);
}
