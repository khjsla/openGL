package com.example.solarlight;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.solarlight.Parser.ObjParser;
import com.example.solarlight.Parser.ObjStructure;

public class SolarSystemRenderer implements GLSurfaceView.Renderer {
    private Context context;

    /* For light and materials */
    //광원 및 물체 반사속성대한 전역변수
    private final static int SUN_LIGHT = GL10.GL_LIGHT0;
    private float[] ambient= { .2f , .2f , .2f , 1.0f };
    private float[] diffuse= { 1.f , 1.f , 1.f , 1.0f };
    private float[] specular= { 0.8f, 0.8f, 0.8f, 1.0f};

    private float[] sun_ambient= { 1.0f, 0.0f, 0.0f, 1.0f };
    private float[] sun_diffuse= { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] sun_specular = { 0.0f, 0.0f, 0.0f, 1.0f };

    private float[] earth_ambient = { 0.0f , 0.0f , 1.0f , 1.0f };
    private float[] earth_diffuse = { 0.5f , 0.5f , 0.5f , 1.0f };
    private float[] earth_specular= { 0.5f, 0.5f, 0.5f, 1.0f };

    private float[] moon_ambient ={ 1.0f, 1.0f, 0.0f, 1.0f };
    private float[] moon_diffuse = {0.5f, 0.5f, 0.5f, 1.0f };
    private float[] moon_specular = {0.5f , 0.5f ,0.0f , 1.0f };

    private float[] mars_ambient ={ 0.0f , 0.0f , 1.0f , 1.0f };
    private float[] mars_diffuse = {0.5f, 0.5f, 0.5f, 1.0f };
    private float[] mars_specular = {0.5f, 0.5f, 0.5f, 1.0f };

    private float[] jupiter_ambient ={0.0f , 0.0f , 1.0f , 1.0f };
    private float[] jupiter_diffuse = {0.5f, 0.5f, 0.5f, 1.0f };
    private float[] jupiter_specular = {0.5f, 0.5f, 0.5f, 1.0f};

    private float[] saturn_ambient ={0.0f , 0.0f , 1.0f , 1.0f };
    private float[] saturn_diffuse = {0.5f, 0.5f, 0.5f, 1.0f };
    private float[] saturn_specular = {0.5f, 0.5f, 0.5f, 1.0f};

    private FloatBuffer makeFloatBuffer(float[] arr){
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    //add Light 함수 정의의
   private void addLight(GL10 gl, int Light_ID, float[] ambient, float[] diff, float[] spec, float[] pos){
        gl.glLightfv(Light_ID, GL10.GL_POSITION, makeFloatBuffer(pos));
        gl.glLightfv(Light_ID, GL10.GL_AMBIENT, makeFloatBuffer(ambient));
        gl.glLightfv(Light_ID, GL10.GL_DIFFUSE, makeFloatBuffer(diff));
        gl.glLightfv(Light_ID, GL10.GL_SPECULAR, makeFloatBuffer(spec));
        gl.glShadeModel(GL10.GL_SMOOTH); // GL_FLAT : 폴리곤에 단색을 입힘, GL_SMOOTH : 각 정점에 해당하는 색상 혼합
        gl.glEnable(Light_ID);
    }

    private void initMaterial(GL10 gl, float[] ambient, float[] diff, float[] spec, float shine){
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, makeFloatBuffer(ambient));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(diff));
        //specular와 shininess를 제거 시 플리커링플리커링현상 많이 감소 .
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(spec));
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shine);
    }

    /* For obj(planet) & texture */
    private ObjStructure[] planet = new ObjStructure[10]; //11개가 있슴

    public int[] texture_id = new int[]{    // 태양, 수성, 금성, 지구, 달, 화성, 목성, 토성, 천왕성, 해왕성
            R.drawable.sun,R.drawable.mercury,R.drawable.venus,
            R.drawable.earth,R.drawable.moon,R.drawable.mars,
            R.drawable.jupiter, R.drawable.saturn, R.drawable.uranus,
            R.drawable.neptune};

    public float scaler = .5f;  // 태양 크기 결정

    public float[] scalefactor = new float[]{   // 태양으로부터 상대적 크기 결정
            scaler, scaler*0.1f, scaler*0.2f,   // 태양, 수성, 금성
            scaler*0.25f, scaler*0.08f, scaler*0.18f,    // 지구, 달, 화성
            scaler*0.5f, scaler*0.4f,scaler*0.3f,scaler*0.3f};  // 목성, 토성, 천왕성, 해왕성

    /* For rotation */
    public boolean rot_flag = true;
    private float rot_sun = 360.0f; private float rot_mercury = 360.0f; private float rot_venus = 360.0f; private float rot_earth = 360.0f; private float rot_moon = 360.0f;
    private float rot_mars = 360.0f; private float rot_jupiter = 360.0f; private float rot_saturn = 360.0f; private float rot_uranus = 360.0f; private float rot_neptune = 360.0f;

    private float angle_mercury = 0.0f;
    private float angle_venus = 0.0f;
    private float angle_earth = 0.0f;
    private float angle_moon = 0.0f;
    private float angle_mars = 0.0f;
    private float angle_jupiter = 0.0f;
    private float angle_saturn = 0.0f;
    private float angle_uranus = 0.0f;
    private float angle_neptune = 0.0f;

    private float orbital = 1.0f;


    /* For camera setting */
    private double distance;
    public volatile double elev;
    public volatile double azim;

    private float[] cam_eye = new float[3];
    private float[] cam_center = new float[3];
    private float[] cam_up = new float[3];
    private float[] cam_vpn = new float[3];
    private float[] cam_x_axis = new float[3];

    private float[] uv_py = new float[3];
    private float[] uv_ny = new float[3];

    /* For texture on, off */
    public boolean texture_on_off = false;

    public SolarSystemRenderer(Context context) {
        this.context = context;
    }

    private void calcCross(float[] vector1, float[] vector2, float[] cp_vector) {
        cp_vector[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
        cp_vector[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
        cp_vector[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];
    }

    private void vNorm(float[] vector) {
        float scale = (float) Math.sqrt(Math.pow((double) vector[0], 2) + Math.pow((double) vector[1], 2) + Math.pow((double) vector[2], 2));

        vector[0] = vector[0] / scale;
        vector[1] = vector[1] / scale;
        vector[2] = vector[2] / scale;
    }

    private void calcUpVector() {
        double r_elev = elev * Math.PI / 180.0;
        double r_azim = azim * Math.PI / 180.0;

        cam_eye[0] = (float) distance * (float) Math.sin(r_elev) * (float) Math.sin(r_azim);
        cam_eye[1] = (float) distance * (float) Math.cos(r_elev);
        cam_eye[2] = (float) distance * (float) Math.sin(r_elev) * (float) Math.cos(r_azim);

        cam_vpn[0] = cam_eye[0] - cam_center[0];
        cam_vpn[1] = cam_eye[1] - cam_center[1];
        cam_vpn[2] = cam_eye[2] - cam_center[2];
        vNorm(cam_vpn);

        if (elev >= 0 && elev < 180) {
            calcCross(uv_py, cam_vpn, cam_x_axis);
        }
        else {
            calcCross(uv_ny, cam_vpn, cam_x_axis);

        }
        calcCross(cam_vpn, cam_x_axis, cam_up);
        vNorm(cam_up);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_FASTEST);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glCullFace(gl.GL_BACK);
        //여기까지 원 본

        // texture activate
        gl.glEnable(gl.GL_TEXTURE);
        gl.glTexEnvf(gl.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

        // light & material activate
        //Light 속성활성화
        gl.glEnable(gl.GL_LIGHTING);
        //material color활성화
        gl.glEnable(gl.GL_COLOR_MATERIAL);

        distance = 10.0;
        elev = 90.0;
        azim = 0.0;

        uv_py[0] = 0.0f;
        uv_py[1] = 1.0f;
        uv_py[2] = 0.0f;

        uv_ny[0] = 0.0f;
        uv_ny[1] = -1.0f;
        uv_ny[2] = 0.0f;

        cam_center[0] = 0.0f;
        cam_center[1] = 0.0f;
        cam_center[2] = 0.0f;

        calcUpVector();

        for(int i=0; i<10;i ++){ //4에서 10으로 바꿔줌
            ObjParser objParser = new ObjParser(context); // obj 파일 Parser 생성
            try {
                objParser.parse(R.raw.planet); // obj 파일 parsing

            } catch (IOException e) {

            }
            int group = objParser.getObjectIds().size(); // 몇 개의 obj 파일이 있는지 확인
            int[] texture = new int[group];
            texture[0] = texture_id[i]; // texture 파일 설정

            planet[i] = new ObjStructure(objParser, gl, this.context, texture); // objstructure 생성
        }
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE); //GL10.GL_REPLACE 를 다른걸로!!
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float zNear = 0.1f;
        float zFar = 1000f;
        float fovy = 45.0f;
        float aspect = (float) width / (float) height;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, fovy, aspect, zNear, zFar);
        gl.glViewport(0, 0, width, height);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //배경색의 정의함수
        gl.glClearDepthf(1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        calcUpVector();

        GLU.gluLookAt(gl, cam_eye[0], cam_eye[1], cam_eye[2], cam_center[0], cam_center[1], cam_center[2], cam_up[0], cam_up[1], cam_up[2]);

        //addlight
        addLight(gl,SUN_LIGHT, ambient, diffuse, specular, new float[]{0.f,0.f,0.f,1.f}); //광원추가

        /**Texture 항상 입히기 = if문에서 glEnable 빼기**/
        //if(texture_on_off){
            gl.glEnable(GL10.GL_TEXTURE_2D);
        //}else{
            //gl.glDisable(GL10.GL_TEXTURE_2D);
        //}


        //0
        gl.glColor4f(1.f,1.f,1.f,1.f); //색이 어두울수록 그림자가 짙어요

        gl.glPushMatrix();

        gl.glRotatef(rot_sun, 0.0f, 1.0f, 0.0f); // 태양의 자전
        // draw Sun

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl, sun_ambient, sun_diffuse, sun_specular,10.0f);

        //gl.glColor4f(1.0f,0.411765f,0.705882f,0.0f); //태양 - 핫핑크 색 and 투명도 1
        planet[0].setScale(scalefactor[0]);
        planet[0].draw(gl);

  /**      //1
        gl.glPushMatrix();
        gl.glRotatef(angle_mercury, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(1.5f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_mercury, 0.0f, 1.0f, 0.0f); //자전
        // draw 수
        //gl.glColor4f(0.960784f,0.960784f,0.862745f,1.0f); //수성 - 베이지 색
        planet[1].setScale(scalefactor[1]);
        planet[1].draw(gl);
        gl.glPopMatrix();

        //2
        gl.glPushMatrix();
        gl.glRotatef(angle_venus, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(2.3f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_venus, 0.0f, 1.0f, 0.0f); //자전
        // draw 금
        //gl.glColor4f(0.956863f,0.643137f,0.376471f,1.0f); //금성 - 샌디 브라운 색
        planet[2].setScale(scalefactor[2]);
        planet[2].draw(gl);
        gl.glPopMatrix();
**/
        //3-1
        gl.glPushMatrix();
        gl.glRotatef(angle_earth, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(3.1f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_earth, 0.0f, 1.0f, 0.0f); //자전
        // draw
        //gl.glColor4f(0.098039f,0.098039f,0.439216f,1.0f); //지구 - 미드나잇 블루색

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl,earth_ambient,earth_diffuse,earth_specular,10.0f);

        planet[3].setScale(scalefactor[3]);
        planet[3].draw(gl);

        //3-2
        gl.glPushMatrix();
        gl.glRotatef(angle_moon, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(0.6f, 0.0f, 0.0f); //위치
        // draw 달
        //gl.glColor4f(1.0f,1.0f,0.878431f,1.0f); //달 - 라이트 옐로우 색

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl,moon_ambient,moon_diffuse,moon_specular,10.0f);

        planet[4].setScale(scalefactor[4]);
        planet[4].draw(gl);
        gl.glPopMatrix();

        gl.glPopMatrix();

        //4
        gl.glPushMatrix();
        gl.glRotatef(angle_mars, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(3.9f, 0.0f, 0.0f);
        gl.glRotatef(rot_mars, 0.0f, 1.0f, 0.0f); //자전
        // draw 화
        //gl.glColor4f(0.737255f,0.560784f,0.560784f,1.0f); //화성 - 로지브라운색

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl,mars_ambient,mars_diffuse,mars_specular,10.0f);

        planet[5].setScale(scalefactor[5]);
        planet[5].draw(gl);
        gl.glPopMatrix();

        //5
        gl.glPushMatrix();
        gl.glRotatef(angle_jupiter, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(4.7f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_jupiter, 0.0f, 1.0f, 0.0f); //자전
        // draw 목
        //gl.glColor4f(0.960784f,0.870588f,0.701961f,1.0f); //목성 - 밀색

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl,jupiter_ambient,jupiter_diffuse,jupiter_specular,10.0f);

        planet[6].setScale(scalefactor[6]);
        planet[6].draw(gl);
        gl.glPopMatrix();

        //6
        gl.glPushMatrix();
        gl.glRotatef(angle_saturn, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(5.8f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_saturn, 0.0f, 1.0f, 0.0f); //자전
        // draw 토
        //gl.glColor4f(0.647059f,0.164706f,0.164706f,1.0f); //토성 - 갈색

        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE,1.f);
        initMaterial(gl,saturn_ambient,saturn_diffuse,saturn_specular,10.0f);

        planet[7].setScale(scalefactor[7]);
        planet[7].draw(gl);
        gl.glPopMatrix();

 /**       //7
        gl.glPushMatrix();
        gl.glRotatef(angle_uranus, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(6.3f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_uranus, 0.0f, 1.0f, 0.0f); //자전
        // draw 천
        //gl.glColor4f(0.529412f,0.807843f,0.980392f,1.0f); //천왕성 - 스카이블루색
        planet[8].setScale(scalefactor[8]);
        planet[8].draw(gl);
        gl.glPopMatrix();

        //8
        gl.glPushMatrix();
        gl.glRotatef(angle_neptune, 0.0f, 1.0f, 0.0f); //공전
        gl.glTranslatef(7.1f, 0.0f, 0.0f); //위치
        gl.glRotatef(rot_neptune, 0.0f, 1.0f, 0.0f); //자전
        // draw 해
        //gl.glColor4f(0.0f,0.0f,0.501961f,1.0f); //해왕성 - 네이비색
        planet[9].setScale(scalefactor[9]);
        planet[9].draw(gl);
        gl.glPopMatrix();
**/
        gl.glPopMatrix();
        //0 의 끝 = 태양계 종료료



        if(rot_flag) {
            rot_sun -= 0.2f; //태양의 자전
            rot_mercury -= 0.5f; //수 자전
            rot_venus -= 0.5f; //금 자전
            rot_earth -= 1.0f; //지 자전
            rot_mars -= 0.5f; //화 자전
            rot_jupiter -= 1.5f; //목 자전
            rot_saturn -= 1.5f; //토 자전
            rot_uranus -= 0.5f; //천 자전
            rot_neptune -= 0.5f; //해 자전

            angle_mercury += orbital*1.7; //태양 주위를 360도 공전합니다
            angle_venus += orbital*2; //태양 주위를 360도 공전합니다
            angle_earth += orbital; //태양 주위를 360도 공전합니다
            angle_moon += orbital*3; //지구 주위를 360도 공전합니다
            angle_mars += orbital*0.4; //태양 주위를 360도 공전합니다
            angle_jupiter += orbital*0.8; //태양 주위를 360도 공전합니다
            angle_saturn += orbital*0.6; //태양 주위를 360도 공전합니다
            angle_uranus += orbital*0.75; //태양 주위를 360도 공전합니다
            angle_neptune += orbital*0.5; //태양 주위를 360도 공전합니다

            if (angle_mercury >= 360.0f) {
                angle_mercury -= 360.0f;
            }
            if (angle_venus >= 360.0f) {
                angle_venus -= 360.0f;
            }
            if (angle_earth >= 360.0f) {
                angle_earth -= 360.0f;
            }
            if (angle_moon >= 360.0f) {
                angle_moon-= 360.0f;
            }
            if (angle_mars >= 360.0f) {
                angle_mars -= 360.0f;
            }
            if (angle_jupiter >= 360.0f) {
                angle_jupiter -= 360.0f;
            }
            if (rot_saturn >= 360.0f) {
                angle_saturn -= 360.0f;
            }
            if (angle_uranus >= 360.0f) {
                angle_uranus -= 360.0f;
            }
            if (angle_neptune >= 360.0f) {
                angle_neptune -= 360.0f;
            }

        }
        gl.glFlush();
    }


}
