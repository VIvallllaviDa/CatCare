package com.daniulive.smartplayer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class GuestureActivity extends Activity {

    private Button buttonSend1 = null;
    private  GestureDetector detector;


    private ImageView iv_canvas;
    private ImageView iv_move;

    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;

    Context mContext = GuestureActivity.this;


    Resources res;

    BitmapDrawable bmpDraw;

    Bitmap bmp;

    Bitmap bmp1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesture);

        res = mContext.getResources();
        bmpDraw =(BitmapDrawable)res.getDrawable(R.drawable.liangban);
        bmp = bmpDraw.getBitmap();
        bmp1 = BitmapFactory.decodeResource(res, R.drawable.liangban);

        // 初始化一个画笔，笔触宽度为5，颜色为红色
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
        iv_canvas.setBackgroundColor(Color.WHITE);
        iv_canvas.setOnTouchListener(touch);

        iv_move = (ImageView) findViewById(R.id.iv_move);
        iv_move.setImageBitmap(bmp1);

        buttonSend1 = (Button)findViewById(R.id.buttonSend1);


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


    private View.OnTouchListener touch = new OnTouchListener() {
        // 定义手指开始触摸的坐标
        float startX;
        float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                // 用户按下动作
                case MotionEvent.ACTION_DOWN:


                    break;
                // 用户手指在屏幕上移动的动作
                case MotionEvent.ACTION_MOVE:
                    // 记录移动位置的点的坐标
                    float stopX = event.getX();
                    float stopY = event.getY();
                    SettingActivity.strMessage ="20:"+ stopX + ":" + stopY + "   \n";
                    new Thread(SettingActivity.sendThread).start();

                    //iv_move.setX(stopX);
                    //iv_move.setY(stopY);


                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
            }
            return true;
        }
    };



}
