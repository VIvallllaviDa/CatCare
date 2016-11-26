package com.daniulive.smartplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class GuestureActivity extends Activity implements GestureDetector.OnGestureListener{

    private Button buttonSend1 = null;
    private  GestureDetector detector;

    private int PlayMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesture);


        buttonSend1 = (Button)findViewById(R.id.buttonSend1);
        detector = new GestureDetector(this, this);

        //发送按钮的监听器
        buttonSend1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SettingActivity.strMessage = "hello spt\n";
                new Thread(SettingActivity.sendThread).start();
            }
        });

    }

    //下面实现的这些接口负责处理所有在该Activity上发生的触碰屏幕相关的事件
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
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
       SettingActivity.strMessage ="20:"+ e2.getX() + ":" + e2.getY() + "   \n";
        new Thread(SettingActivity.sendThread).start();
        Log.i(getClass().getName(),
                "onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                        + e2.getX() + "," + e2.getY() + ")");
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        SettingActivity.strMessage = "hello spt\n";
        new Thread(SettingActivity.sendThread).start();
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


}
