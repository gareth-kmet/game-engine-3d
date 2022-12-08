package game.ai.astar;
import org.lwjgl.util.vector.Vector2f;

public class Line {
	
	private static final float VERTICAL_LINE_GRADIENT = 1e5f;
	
	private float gradient, gradientPerpendicular;
	@SuppressWarnings("unused")
	private float yIntercept;
	private Vector2f pointOnLine1, pointOnLine2;
	private boolean approachSide=false;
	
	public Line(Vector2f pointOnLine, Vector2f pointPerpendicularToLine) {
		float dx = pointOnLine.getX() - pointPerpendicularToLine.getX();
		float dy = pointOnLine.getY() - pointPerpendicularToLine.getY();
		
		if(dx == 0) {
			gradientPerpendicular = VERTICAL_LINE_GRADIENT;
		}else {
			gradientPerpendicular = dy / dx;
		}
		
		if(gradientPerpendicular == 0) {
			gradient = VERTICAL_LINE_GRADIENT;
		}else {
			gradient = -1/gradientPerpendicular;
		}
		
		yIntercept = pointOnLine.getY() - gradient * pointOnLine.getX();
		pointOnLine1=pointOnLine;
		pointOnLine2=Vector2f.add(pointOnLine, new Vector2f(1,gradient), null);
		
		approachSide = getSide(pointPerpendicularToLine);
	}
	
	public boolean getSide(Vector2f p) {
		return (p.getX()-pointOnLine1.getX())*(pointOnLine2.getY()-pointOnLine1.getY())>(p.getY()-pointOnLine1.getY())*(pointOnLine2.getX()-pointOnLine1.getX());
	}
	
	public boolean hasCrossedline(Vector2f p) {
		return getSide(p) != approachSide;
	}

}
