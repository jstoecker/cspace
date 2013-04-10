package cspace.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jgl.math.vector.Vec2d;
import cspace.scene.ArcShape.Edge;
import cspace.scene.ArcShape.Vertex;

public class CSpaceReader {

  private CSpace cspace;
  private BufferedReader in;
  private List<String> lineTokens;
  private int curToken;

  public CSpace read(File file) throws FileNotFoundException {

    cspace = new CSpace();
    try {
      in = new BufferedReader(new FileReader(file));
      readArcShape(cspace.obstacle);
      readArcShape(cspace.robot);
      readEvents();
      readSumVEs();
      readSumEVs();
      readSumEEs();
      readSumSSs();
      readSubEdges();
      cspace.init();
      in.close();
    } catch (NoSuchElementException e) {
      System.err.println("Problem parsing cspace input:\n" + e.getMessage());
    } catch (IOException e) {
      System.err.println("Problem parsing cspace input:\n" + e.getMessage());
    }

    return cspace;
  }

  private void readArcShape(ArcShape shape) throws IOException {
    shape.v = new Vertex[nextInt()];
    for (int i = 0; i < shape.v.length; i++) {
      Vec2d p = nextVec2d();
      Vec2d n = nextVec2d();
      shape.v[i] = shape.new Vertex(p, n, i);
    }

    shape.e = new Edge[nextInt()];
    for (int i = 0; i < shape.e.length; i++) {
      Vec2d c = nextVec2d();
      double r = nextDouble();
      Vertex head = shape.v[nextInt()];
      Vertex tail = shape.v[nextInt()];
      shape.e[i] = shape.new Edge(c, r, head, tail, i);
    }
  }

  private void readEvents() throws IOException {
    cspace.events = new Event[nextInt()];
    for (int i = 0; i < cspace.events.length; i++) {
      Vec2d u = nextVec2d();
      Vec2d p = nextVec2d();
      cspace.events[i] = new Event(u, p, i);
    }
  }

  private void readSumVEs() throws IOException {
    cspace.sves = new SumVE[nextInt()];
    for (int i = 0; i < cspace.sves.length; i++) {
      // NOTE: -1 -1 -1 -1 means null / unused
      int iObsVert = nextInt();
      int iRobEdge = nextInt();
      int iStart = nextInt();
      int iEnd = nextInt();
      if (iObsVert != -1) {
        Vertex vert = cspace.obstacle.v[iObsVert];
        Edge edge = cspace.robot.e[iRobEdge];
        Event start = cspace.events[iStart];
        Event end = cspace.events[iEnd];
        cspace.sves[i] = new SumVE(vert, edge, start, end, i);
      }
    }
  }

  private void readSumEVs() throws IOException {
    cspace.sevs = new SumEV[nextInt()];
    for (int i = 0; i < cspace.sevs.length; i++) {
      // NOTE: -1 -1 -1 -1 means null / unused
      int iObsEdge = nextInt();
      int iRobVert = nextInt();
      int iStart = nextInt();
      int iEnd = nextInt();
      if (iObsEdge != -1) {
        Edge edge = cspace.obstacle.e[iObsEdge];
        Vertex vert = cspace.robot.v[iRobVert];
        Event start = cspace.events[iStart];
        Event end = cspace.events[iEnd];
        cspace.sevs[i] = new SumEV(vert, edge, start, end, i);
      }
    }
  }

  private void readSumEEs() throws IOException {
    cspace.sumMap = new HashMap<CSpace.EdgeSum, List<SumEE>>();
    cspace.sees = new SumEE[nextInt()];
    for (int i = 0; i < cspace.sees.length; i++) {
      // format : iObsEdge iRobEdge tailType iTail headType iHead iStartEvent
      // iEndEvent
      // where type 0=VE, 1=EV
      int iObsEdge = nextInt();
      int iRobEdge = nextInt();
      Edge obsEdge = cspace.obstacle.e[iObsEdge];
      Edge robEdge = cspace.robot.e[iRobEdge];

      CSPnt[] list = (nextInt() == 0) ? cspace.sves : cspace.sevs;
      CSPnt tail = list[nextInt()];

      list = (nextInt() == 0) ? cspace.sves : cspace.sevs;
      CSPnt head = list[nextInt()];

      Event start = cspace.events[nextInt()];
      Event end = cspace.events[nextInt()];

      SumEE sum = new SumEE(obsEdge, robEdge, tail, head, start, end, i);
      cspace.sees[i] = sum;

      CSpace.EdgeSum pair = cspace.new EdgeSum(iRobEdge, iObsEdge);
      List<SumEE> sums = cspace.sumMap.get(pair);
      if (sums == null)
        cspace.sumMap.put(pair, sums = new ArrayList<SumEE>());
      sums.add(sum);
    }
  }

  private void readSumSSs() throws IOException {
    cspace.intns = new Intn[nextInt()];
    for (int i = 0; i < cspace.intns.length; i++) {
      Edge obsEdge0 = cspace.obstacle.e[nextInt()];
      Edge robEdge0 = cspace.robot.e[nextInt()];
      Edge obsEdge1 = cspace.obstacle.e[nextInt()];
      Edge robEdge1 = cspace.robot.e[nextInt()];
      Event start = cspace.events[nextInt()];
      Event end = cspace.events[nextInt()];
      Edge[] eO = { obsEdge0, obsEdge1 };
      Edge[] eR = { robEdge0, robEdge1 };
      cspace.intns[i] = new Intn(eO, eR, start, end, i);
    }
  }

  private void readSubEdges() throws IOException {
    cspace.subs = new Sub[nextInt()];
    for (int i = 0; i < cspace.subs.length; i++) {
      // format : iObsEdge iRobEdge tailType iTail headType iHead iStartEvent
      // iEndEvent
      Edge obsEdge = cspace.obstacle.e[nextInt()];
      Edge robEdge = cspace.robot.e[nextInt()];

      CSPnt tail = null;
      switch (nextInt()) {
      case 0:
        tail = cspace.sves[nextInt()];
        break;
      case 1:
        tail = cspace.sevs[nextInt()];
        break;
      case 2:
        tail = cspace.intns[nextInt()];
        break;
      }

      CSPnt head = null;
      switch (nextInt()) {
      case 0:
        head = cspace.sves[nextInt()];
        break;
      case 1:
        head = cspace.sevs[nextInt()];
        break;
      case 2:
        head = cspace.intns[nextInt()];
        break;
      }

      Event start = cspace.events[nextInt()];
      Event end = cspace.events[nextInt()];

      cspace.subs[i] = new Sub(obsEdge, robEdge, tail, head, start, end, i);
    }
  }

  /** Returns the angle of u in [0, 2pi] where the zero vector is frame of ref */
  private static double angleIn2PI(Vec2d u, Vec2d zero) {
    double zeroRads = Math.atan2(zero.y, zero.x);
    if (zeroRads < 0)
      zeroRads += Math.PI * 2.0;

    double rads = Math.atan2(u.y, u.x) - zeroRads;
    while (rads < 0)
      rads += Math.PI * 2.0;

    return rads;
  }

  private static List<String> tokenize(String line) {
    ArrayList<String> values = new ArrayList<String>();

    int start = 0;
    int end = 0;
    while (start < line.length()) {
      while (start < line.length() && line.charAt(start) == ' ')
        start++;
      end = start + 1;
      while (end < line.length() && line.charAt(end) != ' ')
        end++;
      if (start < line.length()) {
        values.add(line.substring(start, end));
        start = end;
      }
    }

    return values;
  }

  private String nextToken() throws IOException {
    if (lineTokens == null || curToken == lineTokens.size()) {
      curToken = 0;
      do {
        lineTokens = tokenize(in.readLine());
      } while (lineTokens.isEmpty());
    }
    return lineTokens.get(curToken++);
  }

  private int nextInt() throws IOException {
    return Integer.parseInt(nextToken());
  }

  private double nextDouble() throws IOException {
    return Double.parseDouble(nextToken());
  }

  private Vec2d nextVec2d() throws IOException {
    return new Vec2d(Double.parseDouble(nextToken()),
        Double.parseDouble(nextToken()));
  }
}
