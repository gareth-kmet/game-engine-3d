package game.physics.collisions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.physics.collisions.PhysicsCollisions.BOUNDING_BOX_COLLISION;
import game.physics.collisions.PhysicsCollisions.BOUNDING_SPHERE_COLLISION;
import game.physics.collisions.PhysicsCollisions.ROTATED_BOUNDING_BOX_COLLISION;
import game.physics.collisions.PhysicsCollisions.Y_ROTATED_BOUNDING_BOX_COLLISION;
import toolbox.Maths;

@SuppressWarnings("unused")
public abstract class PhysicsCollisionMethods {
	
	public static boolean boundingSphere_boundingSphere(
			Vector3f p1, Vector3f r1, RenderEntity e1, BOUNDING_SPHERE_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_SPHERE_COLLISION d2) {
		
		float dist = Vector3f.sub(p1, p2, new Vector3f()).lengthSquared();
		float minDist = d1.getBoundingRadius()*e1.getScale()+d2.getBoundingRadius()*e2.getScale();
		return dist<=minDist*minDist;
		
	}
	
	public static boolean boundingSphere_boundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, BOUNDING_SPHERE_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_BOX_COLLISION d2) {
		
		float radius = d1.getBoundingRadius()*e1.getScale();
		float[] boxMins = manipulateBoundingBox(e2.getScale(), p2, d2.getMinBoundsClone());
		float[] boxMaxs = manipulateBoundingBox(e2.getScale(), p2, d2.getMaxBoundsClone());
		float[] circlePos = {p1.x, p1.y, p1.z};
		
		return boxCircleMultiDimensionsCollision(boxMins, boxMaxs, circlePos, radius, 3);
	}
	
	public static boolean boundingBox_boundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_BOX_COLLISION d2) {
		
		float[] b1Mins = manipulateBoundingBox(e1.getScale(), p1, d1.getMinBoundsClone());
		float[] b1Maxs = manipulateBoundingBox(e1.getScale(), p1, d1.getMaxBoundsClone());
		float[] b2Mins = manipulateBoundingBox(e2.getScale(), p2, d2.getMinBoundsClone());
		float[] b2Maxs = manipulateBoundingBox(e2.getScale(), p2, d2.getMaxBoundsClone());
		return boxCollision(b1Mins, b1Maxs, b2Mins, b2Maxs);
	}
	
	public static boolean yRotatedBoundingBox_yRotatedBoundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, Y_ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, Y_ROTATED_BOUNDING_BOX_COLLISION d2) {
		
		if(e2.getRotY()==0) {
			return yRotatedBoundingBox_boundingBox(p1, r1, e1, d1, p2, r2, e2, d2);
		}else if(e1.getRotY()==0) {
			return yRotatedBoundingBox_boundingBox(p2, r2, e2, d2, p1, r1, e1, d1);
		}
		

		float[] min1 = manipulateBoundingBox(e1.getScale(), p1, d1.getMinBoundsClone());
		float[] min2 = manipulateBoundingBox(e2.getScale(), p2, d2.getMinBoundsClone());
		float[] max1 = manipulateBoundingBox(e1.getScale(), p1, d1.getMaxBoundsClone());
		float[] max2 = manipulateBoundingBox(e2.getScale(), p2, d2.getMaxBoundsClone());
		
		boolean a = (min1[1] <= max2[1] && max1[1] >= min2[1]);
		if(!a)return false;
//		return testCollisionRotatedY(min1, max1, p1, -e1.getRotY(), min2, max2, p2, -e2.getRotY());
		return planeCollisions(PLANE.XZ, min1, max1, min2, max2, p1, p2, -r1.y, -r2.y);
		
	}
	
	public static boolean yRotatedBoundingBox_boundingSphere(
			Vector3f p1, Vector3f r1, RenderEntity e1, Y_ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_SPHERE_COLLISION d2) {
		
		Vector3f spherePos = rotatePointAroundPoint(p1, p2,r1.y);
		return boundingSphere_boundingBox(spherePos, r2, e2, d2, p1, r1, e1, d1);
	}
	
	public static boolean yRotatedBoundingBox_boundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, Y_ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_BOX_COLLISION d2) {
		if(r1.y==0) {
			return boundingBox_boundingBox(p1, r1, e1, d1, p2, r2, e2, d2);
		}
		
		
		
		float[] min1 = manipulateBoundingBox(e1.getScale(), p1, d1.getMinBoundsClone());
		float[] min2 = manipulateBoundingBox(e2.getScale(), p2, d2.getMinBoundsClone());
		float[] max1 = manipulateBoundingBox(e1.getScale(), p1, d1.getMaxBoundsClone());
		float[] max2 = manipulateBoundingBox(e2.getScale(), p2, d2.getMaxBoundsClone());
		
		boolean a = (min1[1] <= max2[1] && max1[1] >= min2[1]);
		if(!a)return false;
//		return testCollisionRotatedY(min1, max1, p1, -e1.getRotY(), min2, max2, p2, 0);
		return planeCollisions(PLANE.XZ, min1, max1, min2, max2, p1, p2, -r1.y, 0);
	}
	@Deprecated
	public static boolean rotatedBoundingBox_boundingSphere(
			Vector3f p1, Vector3f r1, RenderEntity e1, ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_SPHERE_COLLISION d2) {
		if(e1.getRotX()==0&&e1.getRotZ()==0) {return yRotatedBoundingBox_boundingSphere(p1, r1, e1, d1, p2, r2, e2, d2);}
		
		double[][] m = getRotationMatrix(-e1.getRotX(), -e1.getRotY(), -e1.getRotZ());
		Vector3f sphereRotatedPosition = rotatePoint(e1.getPosition(), m, e2.getPosition());
		return boundingSphere_boundingBox(sphereRotatedPosition, r1, e2, d2, p1, r2, e1, d1);
		
	}
	
	@Deprecated
	public static boolean rotatedBoundingBox_boundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, BOUNDING_BOX_COLLISION d2) {
		if(e1.getRotX()==0&&e1.getRotZ()==0) {return yRotatedBoundingBox_boundingBox(p1, r1, e1, d1, p2, r2, e2, d2);}
		
		return false;
		
	}
	
	@Deprecated
	public static boolean rotatedBoundingBox_yRotatedBoundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, Y_ROTATED_BOUNDING_BOX_COLLISION d2) {
		if(e1.getRotX()==0&&e1.getRotZ()==0) {return yRotatedBoundingBox_yRotatedBoundingBox(p1, r1, e1, d1, p2, r2, e2, d2);}
		
		return false;
	}
	
	@Deprecated
	public static boolean rotatedBoundingBox_rotatedBoundingBox(
			Vector3f p1, Vector3f r1, RenderEntity e1, ROTATED_BOUNDING_BOX_COLLISION d1, 
			Vector3f p2, Vector3f r2, RenderEntity e2, ROTATED_BOUNDING_BOX_COLLISION d2) {
		if(e1.getRotX()==0&&e1.getRotZ()==0) {return rotatedBoundingBox_yRotatedBoundingBox(p2, r2, e2, d2, p1, r1, e1, d1);}
		
		return false;
	}
			
//	----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	private static boolean planeCollisions(PLANE plane, float[] min1, float[] max1, float[] min2, float[] max2, Vector3f p1, Vector3f p2, float r1, float r2) {
		
		Vector2f p1d2 = new Vector2f(plane.a(p1), plane.b(p1));
		Vector2f p2d2 = new Vector2f(plane.a(p2), plane.b(p2));
		
		boolean a = testPlaneCollision(plane, p1d2, p2d2, r1, r2, min1, max1, min2, max2);
		if(!a)return false;
		return testPlaneCollision(plane, p2d2, p1d2, r2, r1, min2, max2, min1, max1);
	}
	
	private static boolean testPlaneCollision(PLANE plane, Vector2f p1, Vector2f p2, float r1, float r2, float[] min1, float[] max1, float[] min2, float[] max2) {
		Collection<Vector2f> points1 = getBoundingBoxPoints(plane, min1, max1);
		float[] min1s = new float[2];Arrays.fill(min1s, Float.POSITIVE_INFINITY);
		float[] max1s = new float[2];Arrays.fill(max1s, Float.NEGATIVE_INFINITY);
		
		for(Vector2f point : points1) {
			Vector2f rotate = point;
			if(r1!=0)rotate = rotatePointAroundPoint(p1, rotate, r1);
			if(r2!=0)rotate = rotatePointAroundPoint(p2, rotate, -r2);
			if(min1s[0]>rotate.x)min1s[0]=rotate.x;if(max1s[0]<rotate.x)max1s[0]=rotate.x;
			if(min1s[1]>rotate.y)min1s[1]=rotate.y;if(max1s[1]<rotate.y)max1s[1]=rotate.y;
		}
		
		return 	(min1s[0]<=plane.a(max2) && max1s[0] >= plane.a(min2)) && 
				(min1s[1]<=plane.b(max2) && max1s[1] >= plane.b(min2)); 
	}
	
	@Deprecated
	private static boolean rotatedCollisionBoxes(float[] min1, float[] max1, float[] min2, float[] max2, Vector3f p1, Vector3f p2, Vector3f r1, Vector3f r2) {
		Collection<Vector3f> points1 = getBoundingBoxPoints(min1, max1);
		Collection<Vector3f> points2 = getBoundingBoxPoints(min2, max2);
		
		double[][] m1 = getRotationMatrix(r1.x, r1.y, r1.z);
		double[][] nm1 = getRotationMatrix(-r1.x, -r1.y, -r1.z);
		double[][] m2 = getRotationMatrix(r2.x, r2.y, r2.z);
		double[][] nm2 = getRotationMatrix(-r2.x, -r2.y, -r2.z);
		
		float[] min1s = new float[3];Arrays.fill(min1s, Float.POSITIVE_INFINITY);
		float[] max1s = new float[3];Arrays.fill(max1s, Float.NEGATIVE_INFINITY);
		
		
		for(Vector3f point : points1) {
			Vector3f p1r1 = rotatePoint(p1, m1, point);
			Vector3f p1r2 = rotatePoint(p2, nm2, p1r1);
			if(min1s[0]>p1r2.x)min1s[0]=p1r2.x;if(max1s[0]<p1r2.x)max1s[0]=p1r2.x;
			if(min1s[1]>p1r2.y)min1s[1]=p1r2.y;if(max1s[1]<p1r2.y)max1s[1]=p1r2.y;
			if(min1s[2]>p1r2.z)min1s[2]=p1r2.z;if(max1s[2]<p1r2.z)max1s[2]=p1r2.z;
		}
		
		boolean a = boxCollision(min1s, max1s, min2, max2);
		if(!a) return false;
		
		float[] min2s = new float[3];Arrays.fill(min2s, Float.POSITIVE_INFINITY);
		float[] max2s = new float[3];Arrays.fill(max2s, Float.NEGATIVE_INFINITY);
		
		
		for(Vector3f point : points2) {
			Vector3f p2r1 = rotatePoint(p2, m2, point);
			Vector3f p2r2 = rotatePoint(p1, nm1, p2r1);
			if(min2s[0]>p2r2.x)min2s[0]=p2r2.x;if(max2s[0]<p2r2.x)max2s[0]=p2r2.x;
			if(min2s[1]>p2r2.y)min2s[1]=p2r2.y;if(max2s[1]<p2r2.y)max2s[1]=p2r2.y;
			if(min2s[2]>p2r2.z)min2s[2]=p2r2.z;if(max2s[2]<p2r2.z)max2s[2]=p2r2.z;
		}
		
		boolean b = boxCollision(min1, max1, min2s, max2s);
		return b;
	}
	
	private static enum PLANE{
		XZ{
			@Override
			protected float a(float[] v) {return v[0];}
			@Override
			protected float a(Vector3f v) {return v.x;}
			@Override
			protected float b(float[] v) {return v[2];}
			@Override
			protected float b(Vector3f v) {return v.z;}
			@Override
			protected float r(Vector3f r) {return r.y;}
		},
		XY{
			@Override
			protected float a(float[] v) {return v[0];}
			@Override
			protected float a(Vector3f v) {return v.x;}
			@Override
			protected float b(float[] v) {return v[1];}
			@Override
			protected float b(Vector3f v) {return v.y;}
			@Override
			protected float r(Vector3f r) {return r.z;}
		},
		ZY{
			@Override
			protected float a(float[] v) {return v[2];}
			@Override
			protected float a(Vector3f v) {return v.z;}
			@Override
			protected float b(float[] v) {return v[1];}
			@Override
			protected float b(Vector3f v) {return v.y;}
			@Override
			protected float r(Vector3f r) {return r.x;}
		};
		protected abstract float a(float[] v);
		protected abstract float a(Vector3f v);
		protected abstract float b(float[] v);
		protected abstract float b(Vector3f v);
		protected abstract float r(Vector3f r);
	}
	
	@Deprecated
	private static boolean testCollisionRotatedY(float[] mina, float[] maxa, Vector3f originA, float yRotA, float[] minb, float[] maxb, Vector3f originB, float yRotB) {
		boolean aPointsInBBox = testCollisionRotatedYStage(mina, maxa, originA, yRotA, minb, maxb, originB, yRotB);
		if(aPointsInBBox)return true;
		boolean bPointsInABox = testCollisionRotatedYStage(minb, maxb, originB, yRotB, mina, maxa, originA, yRotA);
		return bPointsInABox;
	}
	@Deprecated
	private static boolean testCollisionRotatedYStage(float[] mina, float[] maxa, Vector3f originA, float yRotA, float[] minb, float[] maxb, Vector3f originB, float yRotB) {
		
		Collection<Vector3f> aPoints = getBoundingBoxPoints(mina, maxa);
		Collection<Vector3f> arotatedAPoints;
		if(yRotA==0) {
			arotatedAPoints = aPoints;
		}else {
			arotatedAPoints = rotatePoints(aPoints, originA, yRotA);
		}
		Collection<Vector3f> negativebrotatedArotatedAPoints;
		if(yRotB==0) {
			negativebrotatedArotatedAPoints = arotatedAPoints;
		}else {
			negativebrotatedArotatedAPoints = rotatePoints(arotatedAPoints, originB, -yRotB);
		}
		
		for(Vector3f aCollisionPoint : negativebrotatedArotatedAPoints) {
			boolean x = minb[0] <= aCollisionPoint.x && aCollisionPoint.x <= maxb[0];
			boolean z = minb[2] <= aCollisionPoint.z && aCollisionPoint.z <= maxb[2];
			boolean y = minb[1] <= aCollisionPoint.y && aCollisionPoint.y <= maxb[1];
			if(x&&z&&y)return true;
		}
		return false;
	}
	private static Collection<Vector3f> rotatePoints(Collection<Vector3f> aPoints, Vector3f originA, float yRotA) {
		Collection<Vector3f> rotatedPoints = new ArrayList<Vector3f>();
		for(Vector3f point : aPoints) {
			rotatedPoints.add(rotatePointAroundPoint(originA, point, yRotA));
		}
		return rotatedPoints;
	}

	@Deprecated
	private static boolean rotatedCollisionBoxs(float[] min1, float[] max1, float[] min2, float[] max2, Vector3f p1, Vector3f p2, Vector3f r1, Vector3f r2) {
		boolean XZPlaneBool = testRotatePlane(PLANE.XZ, min1, max1, min2, max2, p1, p2, r1, r2);
		if(!XZPlaneBool) return false;
		
		boolean XYPlaneBool = testRotatePlane(PLANE.XY, min1, max1, min2, max2, p1, p2, r1, r2);
		if(!XYPlaneBool) return false;
		
		boolean ZYPlaneBool = testRotatePlane(PLANE.ZY, min1, max1, min2, max2, p1, p2, r1, r2);
		return ZYPlaneBool;
		//return true;
	}
	@Deprecated
	private static boolean testRotatePlane(PLANE plane, float[] min1, float[] max1, float[] min2, float[] max2, Vector3f p1, Vector3f p2, Vector3f r1, Vector3f r2) {
		Collection<Vector2f> point1s = getBoundingBoxPoints(plane, min1, max1);
		Collection<Vector2f> point2s = getBoundingBoxPoints(plane, min2, max2);
		
		Vector2f planerP2 = new Vector2f(plane.a(p2), plane.b(p2));
		Vector2f planerP1 = new Vector2f(plane.a(p1), plane.b(p1));
		float rot1 = plane.r(r1);
		float rot2 = plane.r(r2);
		System.out.println(plane);
//		1 local space test
		float mina1 = plane.a(min1); float minb1 = plane.b(min1);
		float maxa1 = plane.a(max1); float maxb1 = plane.b(max1);
		float mina2 = Float.POSITIVE_INFINITY; float minb2 = Float.POSITIVE_INFINITY;
		float maxa2 = Float.NEGATIVE_INFINITY; float maxb2 = Float.NEGATIVE_INFINITY;
		
		for(Vector2f point : point2s) {
			Vector2f pr1 = rotatePointAroundPoint(planerP2, point, rot2);
			Vector2f pr2 = rotatePointAroundPoint(planerP1, pr1, -rot1);
			if(pr2.x<mina2)mina2=pr2.x;if(pr2.x>maxa2)maxa2=pr2.x;
			if(pr2.y<minb2)minb2=pr2.y;if(pr2.y>maxb2)maxb2=pr2.y;
		}
		boolean localSpace1bool = 	(mina1 <= maxa2 && maxa1 >= mina2) &&
									(minb1 <= maxb2 && maxb1 >= minb2);
		
		if(!localSpace1bool)return false;
		System.out.println("a");
//		2 local space test
		
		mina2 = plane.a(min2); minb2 = plane.b(min2);
		maxa2 = plane.a(max2); maxb2 = plane.b(max2);
		mina1 = Float.POSITIVE_INFINITY; minb1 = Float.POSITIVE_INFINITY;
		maxa1 = Float.NEGATIVE_INFINITY; maxb1 = Float.NEGATIVE_INFINITY;
		
		for(Vector2f point : point1s) {
			Vector2f pr1 = rotatePointAroundPoint(planerP1, point, rot1);
			Vector2f pr2 = rotatePointAroundPoint(planerP2, pr1, -rot2);
			if(pr2.x<mina1)mina1=pr2.x;if(pr2.x>maxa1)maxa1=pr2.x;
			if(pr2.y<minb1)minb1=pr2.y;if(pr2.y>maxb1)maxb1=pr2.y;
		}
		
		boolean localSpace2bool = 	(mina2 <= maxa1 && maxa2 >= mina1) &&
									(minb2 <= maxb1 && maxb2 >= minb1);
		
		return localSpace2bool;
	}
	@Deprecated
	private static float[][] getMinMaxsOfDoubleYRotatedPoints(Collection<Vector3f> dpoints, Vector3f originPosa, float yRota, Vector3f originPosb, float yRotb){
		float[][] minMaxs = {{Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY},{Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY}};
		
		for(Vector3f p : dpoints) {
			Vector3f rotP = rotatePointAroundPoint(originPosa, p, yRota);
			rotP = rotatePointAroundPoint(originPosb, rotP, yRotb);
			if(rotP.x<minMaxs[0][0])minMaxs[0][0]=rotP.x;
			if(rotP.x>minMaxs[1][0])minMaxs[1][0]=rotP.x;
			if(rotP.y<minMaxs[0][1])minMaxs[0][1]=rotP.y;
			if(rotP.y>minMaxs[1][1])minMaxs[1][1]=rotP.y;
			if(rotP.z<minMaxs[0][2])minMaxs[0][2]=rotP.z;
			if(rotP.z>minMaxs[1][2])minMaxs[1][2]=rotP.z;
		}
		return minMaxs;
	}
	@Deprecated
	private static float[][] getMinMaxsOfYRotatedPoints(Collection<Vector3f> dpoints, Vector3f originPos, float yRot) {
		float[][] minMaxs = {{Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY},{Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY}};
		
		for(Vector3f p : dpoints) {
			Vector3f rotP = rotatePointAroundPoint(originPos, p, yRot);
			if(rotP.x<minMaxs[0][0])minMaxs[0][0]=rotP.x;
			if(rotP.x>minMaxs[1][0])minMaxs[1][0]=rotP.x;
			if(rotP.y<minMaxs[0][1])minMaxs[0][1]=rotP.y;
			if(rotP.y>minMaxs[1][1])minMaxs[1][1]=rotP.y;
			if(rotP.z<minMaxs[0][2])minMaxs[0][2]=rotP.z;
			if(rotP.z>minMaxs[1][2])minMaxs[1][2]=rotP.z;
		}
		return minMaxs;
	}
	
	private static boolean boxCollision(float[] b1Mins, float[] b1Maxs, float[] b2Mins, float[] b2Maxs) {
		return 
				(b1Mins[0] <= b2Maxs[0] && b1Maxs[0] >= b2Mins[0]) &&
				(b1Mins[1] <= b2Maxs[1] && b1Maxs[1] >= b2Mins[1]) &&
				(b1Mins[2] <= b2Maxs[2] && b1Maxs[2] >= b2Mins[2]);
	}
	@Deprecated
	private static boolean boxPointsWithinRotBox(float[] boxPointsMins, float[] boxPointsMaxs, float[] boxMins, float[] boxMaxs, Vector3f boxPos, float boxRot) {
		Collection<Vector3f> boxPoints = getBoundingBoxPoints(boxPointsMins, boxPointsMaxs);
		Collection<Vector3f> boxRotPoints = new HashSet<Vector3f>();
		boxPoints.forEach(point->{
			boxRotPoints.add(rotatePointAroundPoint(boxPos, point, boxRot));
		});
		return pointsWithinBox(boxMaxs, boxMins, boxPoints);
	}
	@Deprecated
	private static boolean pointsWithinBox(float[] mins, float[] maxs, Collection<Vector3f> points) {
		for(Vector3f point : points) {
			boolean c = 
					(mins[0]<=point.x && maxs[0]>=point.x) &&
					(mins[1]<=point.y && maxs[1]>=point.y) &&
					(mins[2]<=point.z && maxs[2]>=point.z);
			if(c) return true;
		}
		return false;
	}
	
	
	
	private static Collection<Vector3f> getBoundingBoxPoints(float[] mins, float[] maxs){
		float[] ab[] = {mins,maxs};
		Collection<Vector3f> points = new ArrayList<Vector3f>();
		for(int i=0; i<2; i++)for(int j=0; j<2; j++) for(int k=0; k<2; k++){
			points.add(new Vector3f(ab[i][0],ab[j][1],ab[k][2]));
		}
		return points;
	}
	private static Collection<Vector2f> getBoundingBoxPoints(PLANE plane, float[] mins, float[] maxs){
		float[] ab[] = {mins,maxs};
		Collection<Vector2f> points = new ArrayList<Vector2f>();
		for(int i=0; i<2; i++)for(int j=0; j<2; j++) for(int k=0; k<2; k++){
			Vector3f p = new Vector3f(ab[i][0],ab[j][1],ab[k][2]);
			points.add(new Vector2f(plane.a(p),plane.b(p)));
			
		}
		return points;
	}
	
	private static Vector3f rotatePointAroundPoint(Vector3f origin, Vector3f point, float theta) {
		Vector2f rotate= rotatePointAroundPoint(new Vector2f(origin.x, origin.z), new Vector2f(point.x, point.z), theta);
		return new Vector3f(rotate.x, point.y, rotate.y);
	}
	private static Vector2f rotatePointAroundPoint(Vector2f origin, Vector2f point, float theta) {
		Vector2f pointAround0_0origin = Vector2f.sub(point, origin, new Vector2f());
		Vector2f rotatedPoint = rotatePoint(pointAround0_0origin, theta);
		Vector2f pointRotatedAroundOriginalOrigin = Vector2f.add(rotatedPoint, origin, new Vector2f());
		return pointRotatedAroundOriginalOrigin;
	}
	private static Vector2f rotatePoint(Vector2f point, float theta) {
		float x = (float) (point.x*Maths.cos(theta)-point.y*Maths.sin(theta));
		float y = (float) (point.y*Maths.cos(theta)+point.x*Maths.sin(theta));
		return new Vector2f(x,y);
	}
	
	
	private static Vector3f rotatePoint(Vector3f origin, double[][] m, Vector3f point) {
		float px = point.x - origin.x;
		float py = point.y - origin.y;
		float pz = point.z - origin.z;
		
		Vector3f rot = new Vector3f();
		rot.x = (float) (m[0][0]*px + m[0][1]*py + m[0][2]*pz);
		rot.y = (float) (m[1][0]*px + m[1][1]*py + m[1][2]*pz);
		rot.z = (float) (m[2][0]*px + m[2][1]*py + m[2][2]*pz);
	
		Vector3f.add(rot, origin, rot);
		return rot;
		
	}
	
	private static double[][] getRotationMatrix(float pitch, float yaw, float roll) {
		double cosa = Maths.cos(yaw);
		double sina = Maths.sin(yaw);
		double cosb = Maths.cos(pitch);
		double sinb = Maths.sin(pitch);
		double cosc = Maths.cos(roll);
		double sinc = Maths.sin(roll);
		
		double[][] m = new double[3][3];
		m[0][0] = cosa*cosb;
		m[0][1] = cosa*sinb*sinc - sina*cosc;
		m[0][2] = cosa*sinb*cosc + sina*sinc;
		m[1][0] = sina*cosb;
		m[1][1] = sina*sinb*sinc + cosa*cosa;
		m[1][2] = sina*sinb*cosc - cosa*sinc;
		m[2][0] = -sinb;
		m[2][1] = cosb*sinc;
		m[2][2] = cosb*cosc;
		return m;
	}
	
	
	private static boolean boxCircleMultiDimensionsCollision(float[] boxMins, float[] boxMaxs, float[] circlePos, float radius, int dimensions) {
		float distSqr = radius*radius;
		
		for(int i=0; i<dimensions; i++) {
			if(circlePos[i] < boxMins[i]) distSqr -= Maths.sqr(circlePos[i] - boxMins[i]);
			else if(circlePos[i] > boxMaxs[i]) distSqr -= Maths.sqr(circlePos[i] - boxMaxs[i]);
		}
		
		return distSqr > 0;
	}
	
	private static float[]  manipulateBoundingBox(float scale, Vector3f pos, float[] floats) {
		floats[0] = floats[0] * scale + pos.x;
		floats[1] = floats[1] * scale + pos.y;  
		floats[2] = floats[2] * scale + pos.z;
		return floats;
	}
	

}
