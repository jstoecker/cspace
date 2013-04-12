package cspace.scene;

public class EdgePair {
  public int robEdge;
  public int obsEdge;
  
  public EdgePair() {
  }

  public EdgePair(int robEdge, int obsEdge) {
    this.robEdge = robEdge;
    this.obsEdge = obsEdge;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + obsEdge;
    result = prime * result + robEdge;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != getClass())
      return false;
    EdgePair that = (EdgePair) obj;
    return (robEdge == that.robEdge && obsEdge == that.obsEdge);
  }
}