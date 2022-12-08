package rendering.loaders;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import game.physics.collisions.loaders.CollisionDataLoader;
import objects.models.RawModel;


public class Loader {
	/**
	 * Change this string to match the location of the RES_FOLDER project on the computer
	 */
	public static final String RES_LOCATION_ON_COMPUTER = "C:/Users/garet/Desktop/Programing/Java/Projects/Project E";
	
	public static final String RES_LOCATION = RES_LOCATION_ON_COMPUTER+"/RES FOLDER/res";
	
	CollisionDataLoader currentCollisionDataLoader;
	
	public sealed interface LoaderInfo{
		public VAO loadToVAO(Loader loader);
		public int[] indices();
		public default RawModel createRawModel() {
			return new RawModel(new LoaderVaoIdentity(this), indices().length);
		}
	}
	
	public static record LoaderInfo3f1i(float[] positions, float[] textureCoords, float[] normals, int[] indices) implements LoaderInfo{
		@Override public VAO loadToVAO(Loader loader) {return loader.loadToVAO(this);}
	};
	
	public VAO loadToVAO(LoaderInfo3f1i info) {
		VAO vao = createVAO();
		bindIndicesVBO(info.indices, vao);
		storeDataInAttributeList(0, 3, info.positions, vao);
		storeDataInAttributeList(1, 2, info.textureCoords, vao);
		storeDataInAttributeList(2, 3, info.normals, vao);
		unbindVAO();
		return vao;
	}
	
	public static record LoaderInfo_f1i(int[] indices, int[] lengths, float[]...floats) implements LoaderInfo{
		@Override public VAO loadToVAO(Loader loader) {return loader.loadToVAO(this);}
	};
	
	public VAO loadToVAO(LoaderInfo_f1i info) {
		if(info.lengths.length != info.floats.length) {
			throw new IllegalArgumentException("floats do not match lengths");
		}
		VAO vao = createVAO();
		bindIndicesVBO(info.indices, vao);
		for(int i=0; i<info.floats.length; i++) {
			storeDataInAttributeList(i, info.lengths[i], info.floats[i], vao);
		}
		unbindVAO();
		return vao;
	}
	
	public static record LoaderInfo4f1i(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices) implements LoaderInfo{
		@Override public VAO loadToVAO(Loader loader) {return loader.loadToVAO(this);}
	};
	
	public VAO loadToVAO(LoaderInfo4f1i info) {
		VAO vao = createVAO();
		bindIndicesVBO(info.indices, vao);
		storeDataInAttributeList(0, 3, info.positions, vao);
		storeDataInAttributeList(1, 3, info.textureCoords, vao);
		storeDataInAttributeList(2, 3, info.normals, vao);
		storeDataInAttributeList(3, 3, info.tangents, vao);
		unbindVAO();
		return vao;
	}
	
	/**
	 * Loader for Animation
	 */
	public static record LoaderInfo4f2i(int[] indices, float[] vertices, float[] texCoords, float[] normals, int[] joints, float[] weights) implements LoaderInfo{
		@Override public VAO loadToVAO(Loader loader) {return loader.loadToVAO(this);}
	};
	
	public VAO loadToVAO(LoaderInfo4f2i info) {
		VAO vao = createVAO();
		bindIndicesVBO(info.indices, vao);
		storeDataInAttributeList(0, 3, info.vertices, vao);
		storeDataInAttributeList(1, 3, info.texCoords, vao);
		storeDataInAttributeList(2, 3, info.normals, vao);
		storeDataInAttributeList(4, 3, info.joints, vao);
		storeDataInAttributeList(5, 3, info.weights, vao);
		unbindVAO();
		return vao;
	}
	
	/**
	 * Loader for GUIS
	 */
	public static record LoaderInfo2f0i(float[] positions, float[] textureCoords) implements LoaderInfo{
		@Override public VAO loadToVAO(Loader loader) {return loader.loadToVAO(this);}
		@Override public int[] indices() {throw new IllegalStateException();}
	};
	
	public VAO loadToVAO(LoaderInfo2f0i info) {
		VAO vao = createVAO();
		storeDataInAttributeList(0, 2, info.positions, vao);
		storeDataInAttributeList(1, 2, info.textureCoords, vao);
		unbindVAO();
		return vao;
	}
	
	public VAO loadToVAO(float[] positions, float[] textureCoords) {
		return new LoaderInfo2f0i(positions, textureCoords).loadToVAO(this);
	}
	
	public int loadCubeMap(String[] textureFiles) {
		TEX tex = new TEX(GL11.glGenTextures());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, tex.id);
		
		for(int i=0;i<textureFiles.length; i++) {
			TextureData data = decodeTextureFile(RES_LOCATION+"/"+textureFiles[i]+".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		return tex.id;
	}
	
	public VBO createEmptyVbo(int floatCount, VAO vao) {
		int vboId = GL15.glGenBuffers();
		VBO vbo;
		if(vao!=null) {
			vbo = vao.addVBO(vboId);
		}else {
			vbo = new VBO(vboId);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4,  GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	public VBO createEmptyVbo(int floatCount) {
		return createEmptyVbo(floatCount, null);
	}
	
	public void addInstanceAttribute(int vao, int vbo, int attribute, int dataSize, int instanceDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instanceDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity()*4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions) {
		VAO vao = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions, vao);
		unbindVAO();
		return new RawModel(new LoaderVaoIdentity(vao), positions.length/2);
	}
	
	public TEX loadGameTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(RES_LOCATION+"/"+fileName+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0f);
			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}else {
				System.out.println("Not supported");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TEX tex = new TEX(texture);
		return tex;
	}
	
	public int loadFontTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(RES_LOCATION+"/"+fileName+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TEX tex = new TEX(texture);
		return tex.id;
	}
	
	public VAO createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		return new VAO(vaoID);
	}
	
	
	public void cleanUp() {
		Storage.cleanUp();
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data, VAO vao) {
		int vboID = GL15.glGenBuffers();
		vao.addVBO(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, int[] data, VAO vao) {
		int vboID = GL15.glGenBuffers();
		vao.addVBO(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInInteBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attributeNumber,coordinateSize,GL11.GL_INT,coordinateSize*4,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesVBO(int[] indices, VAO vao) {
		int vboID=GL15.glGenBuffers();
		vao.addVBO(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInInteBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer storeDataInInteBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public CollisionDataLoader getCurrentCollisionDataLoader() {
		return currentCollisionDataLoader;
	}

	public void setCurrentCollisionDataLoader(CollisionDataLoader currentCollisionDataLoader) {
		this.currentCollisionDataLoader = currentCollisionDataLoader;
	}

	private static sealed abstract class Storage{
		
		public final int id;
		
		private Storage(int id) {
			this.id=id;
		}
		
		public void delete() {
			delete(true);
		}
		protected abstract void delete(boolean remove);
		
		private static ArrayList<VAO> vaos = new ArrayList<VAO>();
		private static ArrayList<VBO> vbos = new ArrayList<VBO>();
		private static ArrayList<TEX> textures = new ArrayList<TEX>();
		
		
		private static void cleanUp() {
			for(VAO vao:vaos) {
				vao.delete(false);
			}
			for(VBO vbo:vbos) {
				vbo.delete(false);
			}
			for(TEX texture : textures) {
				texture.delete(false);
			}
			textures.clear();
			vaos.clear();
			vbos.clear();
			
		}
		
		public static VAO getVAO(int id) {
			for(VAO vao : vaos) {
				if(vao.id == id) {
					return vao;
				}
			}
			return null;
		}
		
		public static VBO getVBO(int id, VAO vao) {
			return getVBO(id, vao.vbos);
		}
		
		public static VBO getVBO(int id) {
			return getVBO(id, vbos);
		}
		
		private static VBO getVBO(int id, List<VBO> vbos) {
			for(VBO vbo : vbos) {
				if(vbo.id==id) {
					return vbo;
				}
			}
			return null;
		}
		
		public static VAO getVAOforVBO(int id) {
			for(VAO vao : vaos) {
				VBO vbo = getVBO(id, vao);
				if(vbo!=null) {
					return vao;
				}
			}
			return null;
		}
	}
	
	public static final class VAO extends Storage{
		
		private ArrayList<VBO> vbos = new ArrayList<VBO>();
		
		private VAO(int id, VBO ... vbos) {
			super(id);
			for(VBO vbo : vbos) {
				this.vbos.add(vbo);
			}
			Storage.vaos.add(this);
		}
		
		private VBO addVBO(int id) {
			VBO vbo = new VBO(id);
			vbos.add(vbo);
			return vbo;
		}
		
		@Override
		protected void delete(boolean remove) {
			for(VBO vbo : vbos) {
				vbo.delete(remove);
			}
			GL30.glDeleteVertexArrays(this.id);
			if(remove)Storage.vaos.remove(this);
		}
	}
	
	public static final class VBO extends Storage{
		
		private VBO(int id){
			super(id);
			Storage.vbos.add(this);
		}
		
		@Override
		protected void delete(boolean remove) {
			GL15.glDeleteBuffers(this.id);
			if(remove)Storage.vbos.remove(this);
		}
	}
	
	public static final class TEX extends Storage{
		
		private final Texture tex;
		
		private TEX(Texture tex) {
			super(tex.getTextureID());
			Storage.textures.add(this);
			this.tex=tex;
		}
		
		private TEX(int id) {
			super(id);
			Storage.textures.add(this);
			this.tex=null;
		}
		
		public Texture getTexture() {
			return tex;
		}

		@Override
		protected void delete(boolean remove) {
			GL11.glDeleteTextures(id);
			if(remove)Storage.textures.remove(this);
		}
		
		
	}
	
	private static HashSet<Storage> needsToBeDeleted = new HashSet<Storage>();
	private static HashSet<Storage> deleted = new HashSet<Storage>();
	
	public static void addElementToDeletionList(Storage s) {
		needsToBeDeleted.add(s);
	}
	
	public static void deleteRequiringElements() {
		HashSet<Storage> temp = deleted;
		deleted = needsToBeDeleted;
		needsToBeDeleted = temp;
		
		deleted.forEach(s->s.delete());
		deleted.clear();
	}

}
