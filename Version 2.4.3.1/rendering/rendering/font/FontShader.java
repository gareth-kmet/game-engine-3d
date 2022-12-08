package rendering.font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import rendering.ShaderProgram;

class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "rendering/rendering/font/fontVertex.txt";
	private static final String FRAGMENT_FILE = "rendering/rendering/font/fontFragment.txt";
	
	private int 
		location_colour,
		location_translation,
		location_width,
		location_edge,
		location_borderWidth,
		location_boarderEdge,
		location_offset,
		location_outlineColour
	;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_width = super.getUniformLocation("width");
		location_edge = super.getUniformLocation("edge");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_boarderEdge = super.getUniformLocation("boarderEdge");
		location_offset = super.getUniformLocation("offset");
		location_outlineColour = super.getUniformLocation("outlineColour");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");

	}
	
	protected void loadEffects(GUIText text) {
		super.loadFloat(location_width, text.getWidth());
		super.loadFloat(location_edge, text.getEdge());
		super.loadFloat(location_borderWidth, text.getBorderWidth());
		super.loadFloat(location_boarderEdge, text.getBoarderEdge());
		super.loadVector(location_offset, text.getOffset());
		super.loadVector(location_outlineColour, text.getOutlineColour());
	}
	
	protected void loadColour(Vector3f colour) {
		super.loadVector(location_colour, colour);
	}
	
	protected void loadTranslation(Vector2f translation) {
		super.loadVector(location_translation, translation);
	}


}
