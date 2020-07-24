package com.example.solarlight;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private final double TOUCH_SCALE_FACTOR = 0.1;
    private double preAzim;
    private double preElev;
    private GLSurfaceView surfaceView;
    private SolarSystemRenderer solarSystem = new SolarSystemRenderer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);

        surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceView.setRenderer(solarSystem);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceView.setZOrderOnTop(true);

        addContentView(surfaceView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        double azim = e.getX();
        double elev = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                double dAzim = azim - preAzim;
                double dElev = elev - preElev;

                solarSystem.azim += dAzim * TOUCH_SCALE_FACTOR;
                solarSystem.elev += dElev * TOUCH_SCALE_FACTOR;

                if(solarSystem.azim > 360.0f) {
                    solarSystem.azim -= 360.0f;
                }

                if(solarSystem.azim < 0.0f) {
                    solarSystem.azim += 360.0f;
                }

                if(solarSystem.elev > 360.0f) {
                    solarSystem.elev -= 360.0f;
                }

                if(solarSystem.elev < 0.0f) {
                    solarSystem.elev += 360.0f;
                }

                surfaceView.requestRender();
        }

        preAzim = azim;
        preElev = elev;
        return true;
    }

/**    public void texture_on_off(View view){
        solarSystem.texture_on_off = !solarSystem.texture_on_off;
    }**/

    public void rotation_on_off(View view){
        solarSystem.rot_flag = !solarSystem.rot_flag;
    }
}
