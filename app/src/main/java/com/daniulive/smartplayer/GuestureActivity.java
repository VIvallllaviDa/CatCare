package com.daniulive.smartplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GuestureActivity extends Activity {


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

    }

    public static void updating_view(Integer x, Integer y) {
        if (0 == isRunning) {
            return;
        }

        catX = x;
        catY = y;

//        Integer h = iv_cat.getHeight();
//        Integer w = iv_cat.getWidth();

        System.out.println("updating_view x: " + catX.toString() + " y: " + catY.toString());

        handler.sendEmptyMessage(0);

    }


    private View.OnTouchListener touch = new OnTouchListener() {
        // 定义手指开始触摸的坐标
        float startX;
        float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                // 用户按下动作
                case MotionEvent.ACTION_DOWN:
//                    parms = (RelativeLayout.LayoutParams) iv_move.getLayoutParams();
//                    par = (LinearLayout.LayoutParams) getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLayoutParams();
//                    dx = event.getRawX() - parms.leftMargin;
//                    dy = event.getRawY() - parms.topMargin;

                    break;
                // 用户手指在屏幕上移动的动作
                case MotionEvent.ACTION_MOVE:
                    // 记录移动位置的点的坐标
                    float stopX = event.getX();
                    float stopY = event.getY();
                    SettingActivity.strMessage ="20:"+ stopX + ":" + stopY + "   \n";
                    new Thread(SettingActivity.sendThread).start();

//                    x = event.getRawX();
//                    y = event.getRawY();
//                    parms.leftMargin = (int) (x-dx);
//                    parms.topMargin = (int) (y - dy);
//                    iv_move.setLayoutParams(parms);

                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        isRunning = 0;

    }



}
