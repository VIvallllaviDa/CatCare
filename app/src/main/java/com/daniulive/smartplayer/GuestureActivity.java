package com.daniulive.smartplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class GuestureActivity extends Activity implements GestureDetector.OnGestureListener {

    private  GestureDetector detector;
    int play_mode = 20;

    private static ImageView iv_cat;
    private static Integer catX;
    private static Integer catY;

    private static int isRunning = 0;

    static int w_screen = 0;
    static int h_screen = 0;


    private static Handler handler = new Handler() {

        // 处理子线程给我们发送的消息。
        @Override
        public void handleMessage(android.os.Message msg) {

            System.out.println("Handle message x: " + catX.toString() + " y: " + catY.toString());

            int x = catX * w_screen / 100;
            int y = catY * h_screen / 100;

            MarginLayoutParams margin = new MarginLayoutParams(iv_cat.getLayoutParams());
            margin.setMargins(x, y, x + margin.width, y + margin.height);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
            iv_cat.setLayoutParams(layoutParams);
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesture);

        iv_cat = (ImageView)findViewById(R.id.cat_icon);

        isRunning = 1;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        w_screen = dm.widthPixels;
        h_screen = dm.heightPixels;

        detector = new GestureDetector(this, this);
    }

    public static void updating_view(Integer x, Integer y) {
        if (0 == isRunning) {
            return;
        }
        catX = x;
        catY = y;
        System.out.println("updating_view x: " + catX.toString() + " y: " + catY.toString());
        handler.sendEmptyMessage(0);
    }


    //增加直接在直播时手势识别的系统
    //下面实现的这些接口负责处理所有在该Activity上发生的触碰屏幕相关的事件
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if(play_mode != 20)
            return false;
        return detector.onTouchEvent(e);
    }



    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
                break;
        }
        return name;
    }



    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(getClass().getName(), "onSingleTapUp-----" + getActionName(e.getAction()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(getClass().getName(), "onLongPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x =  e2.getX()/w_screen*100;
        float y =  e2.getY()/h_screen*100;
        //Toast.makeText(SmartPlayer.this, "Get e2 "+e2.getX()+" "+e2.getY(), Toast.LENGTH_SHORT).show();
        SettingActivity.strMessage ="20:"+ x + ":" + y ;
        new Thread(SettingActivity.sendThread).start();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(getClass().getName(),
                "onFling-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                        + e2.getX() + "," + e2.getY() + ")");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(getClass().getName(), "onShowPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(getClass().getName(), "onDown-----" + getActionName(e.getAction()));
        return false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        play_mode = 0;
        SettingActivity.strMessage = String.valueOf(play_mode)+ ":0:0";
        new Thread(SettingActivity.sendThread).start();

        isRunning = 0;

    }



}
