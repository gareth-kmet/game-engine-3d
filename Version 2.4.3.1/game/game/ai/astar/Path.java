package game.ai.astar;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Path {
	
	public final Vector3f[] lookPoints;
	public final Line[] turnBoundaries;
	public final int finishLineIndex;
	
	public Path(Vector3f[] waypoints, Vector3f startPos, float turnDst) {
		lookPoints=waypoints;
		turnBoundaries = new Line[lookPoints.length];
		finishLineIndex = turnBoundaries.length-1;
		
		Vector2f previousPoint = new Vector2f(startPos.x, startPos.z);
		for (int i = 0; i < lookPoints.length; i++) {
			Vector2f currentPoint = new Vector2f(lookPoints[i].x, lookPoints[i].z);
			Vector2f dirToCurrentPoint = Vector2f.sub(currentPoint, previousPoint, null).normalise(null);
			dirToCurrentPoint.scale(turnDst);
			Vector2f turnBoundaryPoint = i==finishLineIndex?currentPoint:Vector2f.sub(currentPoint, dirToCurrentPoint, null);
			Vector2f.sub(previousPoint, dirToCurrentPoint, null);
			turnBoundaries[i] = new Line(turnBoundaryPoint, Vector2f.sub(previousPoint, dirToCurrentPoint, null));
			previousPoint = turnBoundaryPoint;
		}
		
	}

}
