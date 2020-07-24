package com.example.solarlight.Parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by junyoung on 2016-05-24.
 */
public class ObjStructure {

    private ArrayList<FloatBuffer> vertexBuffer = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> normalBuffer = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> textureBuffer = new ArrayList<FloatBuffer>();
    boolean texture_flag , normal_flag;
    private ArrayList<Integer> count = new ArrayList<Integer>();
    float scale = 1.0f;
    int group;
    int[] textureIDs;

    public ObjStructure(ObjParser objParser, GL10 gl, Context context, int[] resourceId) {

        group = objParser.getObjectIds().size();
        texture_flag = objParser.texture_flag;
        if(resourceId==null ) texture_flag = false;
        normal_flag = objParser.normal_flag;
        textureIDs = new int[group];
        gl.glGenTextures(textureIDs.length, textureIDs, 0);

        for(int i=0;i<group;i++) {
            String ID = objParser.getObjectIds().get(i);
            float[] vertices = objParser.getObjectVertices(ID);
            float[] normals = objParser.getObjectNormals(ID);
            float[] textures = objParser.getObjectTextures(ID);

            ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            FloatBuffer vertex_tmp = byteBuf.asFloatBuffer();
            vertex_tmp.put(vertices); // vertices 버퍼 생성
            vertex_tmp.position(0);
            vertexBuffer.add(vertex_tmp);
            count.add(vertices.length / 3);

            if (normal_flag) {
                byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                FloatBuffer normal_tmp = byteBuf.asFloatBuffer();
                normal_tmp.put(normals); // normals 버퍼 생성
                normal_tmp.position(0);
                normalBuffer.add(normal_tmp);
            }

            if (texture_flag) {
                createTexture(gl, context, resourceId[i], i); // 새로운 Texture object 생성 함수
                byteBuf = ByteBuffer.allocateDirect(textures.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                FloatBuffer texture_tmp = byteBuf.asFloatBuffer();
                texture_tmp.put(textures); // texture 버퍼 생성
                texture_tmp.position(0);
                textureBuffer.add(texture_tmp);
            }
        }
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public void draw(GL10 gl) {

        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        for(int i=0;i<group;i++) {
            if (texture_flag) {
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Texture_Coord_ARRAY 활성화
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[i]); // 새로운 texture object 생성
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer.get(i));
            }
            if (normal_flag) {
                gl.glEnableClientState(GL10.GL_NORMAL_ARRAY); // Normal_ARRAY 활성화
                gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer.get(i));
            }
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // VERTEX_ARRAY 활성화
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer.get(i));
            gl.glDrawArrays(gl.GL_TRIANGLES, 0, count.get(i));
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        gl.glPopMatrix();

    }

    public int createTexture(GL10 gl, Context contextRegf, int resource, int num)
    {
        Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // image 파일 load
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[num]);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 읽어온 image 파일을 texture 형식으로 지정

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        tempImage.recycle(); // image파일 메모리 해제

        return resource;
    }
}
