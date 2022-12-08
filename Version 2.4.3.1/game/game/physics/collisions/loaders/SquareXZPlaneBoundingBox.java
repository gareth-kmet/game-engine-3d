package game.physics.collisions.loaders;

import java.util.List;

import rendering.loaders.Vertex;
import toolbox.Maths;

class SquareXZPlaneBoundingBox extends BoundingBox {
	
	enum TYPE{
		MIN {
			@Override
			protected void finish(SquareXZPlaneBoundingBox thiser) {
				float v = Maths.min(true, thiser.xMin, thiser.xMax, thiser.zMin, thiser.zMax);
				thiser.xMin = -v; thiser.xMax=v;
				thiser.zMin = -v; thiser.zMax=v;
			}
		},
		AVERAGE {
			@Override
			protected void finish(SquareXZPlaneBoundingBox thiser) {
				float v = Maths.average(true, thiser.xMin, thiser.xMax, thiser.zMin, thiser.zMax);
				thiser.xMin = -v; thiser.xMax=v;
				thiser.zMin = -v; thiser.zMax=v;
				
			}
		},
		MAX {
			@Override
			protected void finish(SquareXZPlaneBoundingBox thiser) {
				float v = Maths.max(true, thiser.xMin, thiser.xMax, thiser.zMin, thiser.zMax);
				thiser.xMin = -v; thiser.xMax=v;
				thiser.zMin = -v; thiser.zMax=v;
				
			}
		},
		;
		protected abstract void finish(SquareXZPlaneBoundingBox thiser);
	}
	
	private final TYPE type;
	
	protected SquareXZPlaneBoundingBox(game.physics.collisions.data.BoundingBox.Boxes rotatable, TYPE type) {super(rotatable);this.type=type;}
	
	@Override
	public void finish(List<Vertex> vertices) {
		type.finish(this);
		super.finish(vertices);
	}
	

}
