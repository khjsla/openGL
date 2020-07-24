package com.example.solarlight.Parser;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Parser {
	private Map<String, ArrayList<Float>> vertices;
	private Map<String, ArrayList<Float>> normals;
	private Map<String, ArrayList<Float>> textures;
	
	private ArrayList<String> objIds;
	private Map<String, ArrayList<String>> textureFiles;
	
	private String currObjId;
	
	protected BufferedReader file;
	
	protected Context context;
	
	public Parser(Context context) {
		this.context = context;
		this.objIds = new ArrayList<String> ();
		this.vertices = new HashMap<String, ArrayList<Float>>();
		this.normals = new HashMap<String, ArrayList<Float>>();
		this.textures = new HashMap<String, ArrayList<Float>>();
		this.textureFiles = new HashMap<String, ArrayList<String>>();
	}
	
	public void parse(int resId) throws IOException {
		InputStream in = context.getResources().openRawResource(resId);
		InputStreamReader is = new InputStreamReader(in);
		this.file = new BufferedReader(is);
	}
	
	protected void addVertice(Float[] vertice) {
		ArrayList<Float> verts = this.vertices.get(currObjId);
		for(int i=0; i < vertice.length; i++) {
			verts.add(vertice[i]);
		}
	}
	protected void addNormal(Float[] normal) {
		ArrayList<Float> Norms = this.normals.get(currObjId);
		for(int i=0; i < normal.length; i++) {
			Norms.add(normal[i]);
		}
		
	}
	protected void addTexture(Float[] texture) {
		ArrayList<Float> Texts = this.textures.get(currObjId);
		for(int i=0; i < texture.length; i++) {
			Texts.add(texture[i]);
		}
	}
	protected void addTexturefile(String textureFile) {
		this.textureFiles.get(currObjId).add(textureFile);
	}
	protected void addObjId(String objId) {
		this.currObjId = objId;		
		this.objIds.add(objId);
		
		this.vertices.put(currObjId, new ArrayList<Float> ());
		this.textures.put(currObjId, new ArrayList<Float> ());
		this.normals.put(currObjId, new ArrayList<Float> ());
		this.textureFiles.put(currObjId, new ArrayList<String> ());
	}
	
	public ArrayList<String> getObjectIds() {
		return  this.objIds;
	}
	
	public float[] getObjectVertices(String objId) {
		ArrayList<Float> verts = this.vertices.get(objId);
		
		float[] retArray = new float[verts.size()];
		int i = 0;
		for(Float f : verts) {
			retArray[i++] = (f != null ? f.floatValue() : 0);
		}
		return retArray;
	}
	public float[] getObjectNormals(String objId) {
		ArrayList<Float> norms = this.normals.get(objId);
		
		float[] retArray = new float[norms.size()];
		int i = 0;
		for(Float f : norms) {
			retArray[i++] = (f != null ? f.floatValue() : 0);
		}
		return retArray;
	}
	public float[] getObjectTextures(String objId) {
		ArrayList<Float> texts = this.textures.get(objId);
		float[] retArray = new float[texts.size()];
		int i = 0;
		for(Float f : texts) {
			retArray[i++] = (f != null ? f.floatValue() : 0);
		}
		return retArray;
	}

	public String[] getObjectTextureFiles(String objId) {
		ArrayList<String> textFiles = this.textureFiles.get(objId);
		String[] retArray = new String[textFiles.size()];
		int i = 0;
		for(String s : textFiles) {
			retArray[i++] = s;
		}
		return retArray;
	}




}
