/*
 * SmartPlayer.java
 * SmartPlayer
 * 
 * Github: https://github.com/daniulive/SmarterStreaming
 * 
 * Created by DaniuLive on 2015/09/26.
 * Copyright © 2014~2016 DaniuLive. All rights reserved.
 */

package com.daniulive.smartplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eventhandle.SmartEventCallback;
import com.videoengine.NTRenderer;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.androidinject.annotation.present.AIActionBarActivity;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.smartplayer_activity)
public class SmartPlayer extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,GestureDetector.OnGestureListener{

	private  GestureDetector detector;

	//0表示关闭投影仪
	//1表示第1种方式（老鼠）进行玩耍
	//2表示以第2种方式（鱼）进行玩耍
	//3表示以第3种方式（毛线球）进行玩耍
	//4表示仅仅打开kincet的投影，投影空白或者不动的鱼
	//20表示切换到手动，需要提供后面两个数据作为坐标（百分比）
	int play_mode = 0;
	int w_screen = 0;
	int h_screen = 0;

	private SurfaceView sSurfaceView = null;

	private long playerHandle = 0;

	private static final int PORTRAIT = 1;		//竖屏
	private static final int LANDSCAPE = 2;		//横屏
	private static final String TAG = "SmartPlayer";

	private SmartPlayerJni libPlayer = null;

	private int currentOrigentation = PORTRAIT;
	private boolean isPlaybackViewStarted = false;
	private String playbackUrl = "rtmp://daniulive.com:1935/hls/streamt4";

    private Button button_pause;
	//Button btnPopInputText;
	Button btnStartStopPlayback;


	LinearLayout lLayout = null;
	FrameLayout fFrameLayout = null;
	private Context myContext;

    @AIView(R.id.activity_video_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.activity_video_rfab)
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

	static {
		System.loadLibrary("SmartPlayer");
	}

	@Override protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		w_screen = dm.widthPixels;
		h_screen = dm.heightPixels;
		Toast.makeText(SmartPlayer.this, "Get the size of screen with"+w_screen+" "+h_screen, Toast.LENGTH_SHORT).show();
        //setContentView(R.layout.smartplayer_activity);

		detector = new GestureDetector(this, this);

        button_pause = (Button)findViewById(R.id.button_pause);
        button_pause.setVisibility(View.GONE);

        button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending socket here...
				//发送4给server
				play_mode = 4;
				SettingActivity.strMessage = String.valueOf(play_mode)+ ":0:0";
				new Thread(SettingActivity.sendThread).start();

                rfaBtn.setVisibility(View.VISIBLE);
                rfaBtn.setEnabled(true);
                button_pause.setVisibility(View.GONE);
            }
        });

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Ball")
                .setResId(R.drawable.maoxianqiu)
                .setIconNormalColor(0xff7cb388)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xff652b14)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Fish")
                .setResId(R.drawable.yu)
                .setIconNormalColor(0xff7cb388)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xff652b14)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Mouse")
                .setResId(R.drawable.laoshu)
                .setIconNormalColor(0xff7cb388)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xff652b14)
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Play")
                .setResId(R.drawable.shoudong)
                .setIconNormalColor(0xff7cb388)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xff652b14)
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(context, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(context, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                context,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();

        Log.i(TAG, "Run into OnCreate++");

        libPlayer = new SmartPlayerJni();

        myContext = this.getApplicationContext();

        boolean bViewCreated = CreateView();

        if(bViewCreated){
            inflateLayout(LinearLayout.VERTICAL);
        }

	}

    private void setPlaying(int position) {

        if (position < 3) {
            button_pause.setVisibility(View.VISIBLE);
            rfabHelper.toggleContent();
            rfaBtn.setVisibility(View.GONE);
            rfaBtn.setEnabled(false);
            // sending socket here...
			if (position == 0){
				play_mode = 1;
			} else if (position == 1){
				play_mode = 2;
			} else if (position == 2){
				play_mode = 3;
			}
			SettingActivity.strMessage = String.valueOf(play_mode)+ ":0:0";
			new Thread(SettingActivity.sendThread).start();
        } else {
            //Intent intent = new Intent(SmartPlayer.this,GuestureActivity.class);
            //startActivity(intent);
			button_pause.setVisibility(View.VISIBLE);
			rfabHelper.toggleContent();
			rfaBtn.setVisibility(View.GONE);
			rfaBtn.setEnabled(false);
			play_mode = 20;
        }
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
//        Toast.makeText(getContext(), "clicked label: " + position, Toast.LENGTH_SHORT).show();
//        rfabHelper.toggleContent();
        setPlaying(position);
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
//        Toast.makeText(getContext(), "clicked icon: " + position, Toast.LENGTH_SHORT).show();
//        rfabHelper.toggleContent();
        setPlaying(position);
    }

	/* For smartplayer demo app, the url is based on: baseURL + inputID
     * For example: 
     * baseURL: rtmp://daniulive.com:1935/hls/stream
     * inputID: 123456 
     * playbackUrl: rtmp://daniulive.com:1935/hls/stream123456
     * */
//	private void GenerateURL(String id){
//		if(id == null)
//			return;
//
//		btnStartStopPlayback.setEnabled(true);
//		String baseURL = "rtmp://daniulive.com:1935/hls/streamt4";
//
//		playbackUrl = baseURL + id;
//	}



	/* Popup InputID dialog */
//	private void PopDialog(){
//		final EditText inputID = new EditText(this);
//		inputID.setFocusable(true);
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("如 rtmp://daniulive.com:1935/hls/stream123456,请输入123456").setView(inputID).setNegativeButton(
//				"取消", null);
//		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface dialog, int which) {
//				String strID = inputID.getText().toString();
//				GenerateURL(strID);
//			}
//		});
//		builder.show();
//	}


	/* Generate basic layout */
	private void inflateLayout(int orientation) {
		if (null == lLayout)
			lLayout = new LinearLayout(this);

//		addContentView(lLayout,  new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
//				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


        lLayout.setOrientation(orientation);

		fFrameLayout = new FrameLayout(this);

		LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
		fFrameLayout.setLayoutParams(lp);
		Log.i(TAG, "++inflateLayout..");

		sSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		fFrameLayout.addView(sSurfaceView, 0);

		RelativeLayout outLinearLayout = new RelativeLayout(this);
		outLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

		LinearLayout lLinearLayout = new LinearLayout(this);
		lLinearLayout.setOrientation(LinearLayout.VERTICAL);
		lLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


        /* Start playback stream button */
		btnStartStopPlayback = new Button(this);
		btnStartStopPlayback.setText("开始播放 ");
		btnStartStopPlayback.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		lLinearLayout.addView(btnStartStopPlayback, 0);


		outLinearLayout.addView(lLinearLayout, 0);
		fFrameLayout.addView(outLinearLayout, 1);

        rfaLayout.addView(fFrameLayout, 0);

		if(isPlaybackViewStarted)
		{
			//btnPopInputText.setEnabled(false);
			//btnPopInputUrl.setEnabled(false);
			btnStartStopPlayback.setText("停止播放 ");
		}
		else
		{
			//btnPopInputText.setEnabled(true);
			//btnPopInputUrl.setEnabled(true);
			btnStartStopPlayback.setText("开始播放 ");
		}
        
        /* PopInput button listener */
//		btnPopInputText.setOnClickListener(new Button.OnClickListener() {
//
//			//  @Override
//			public void onClick(View v) {
//				Log.i(TAG, "Run into input playback ID++");
//
//				PopDialog();
//
//				Log.i(TAG, "Run out from input playback ID--");
//			}
//		});




		btnStartStopPlayback.setOnClickListener(new Button.OnClickListener() {

			//  @Override
			public void onClick(View v) {

				if(isPlaybackViewStarted) {
					Log.i(TAG, "Stop playback stream++");
					btnStartStopPlayback.setText("开始播放 ");
					libPlayer.SmartPlayerClose(playerHandle);
					playerHandle = 0;
					isPlaybackViewStarted = false;
					Log.i(TAG, "Stop playback stream--");
				}
				else
				{
					Log.i(TAG, "Start playback stream++");

					playerHandle = libPlayer.SmartPlayerInit(myContext);

					if(playerHandle == 0)
					{
						Log.e(TAG, "surfaceHandle with nil..");
						return;
					}

					libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());

					libPlayer.SmartPlayerSetSurface(playerHandle, sSurfaceView); 	//if set the second param with null, it means it will playback audio only..

					// libPlayer.SmartPlayerSetSurface(playerHandle, null);

					libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);

					libPlayer.SmartPlayerSetBuffer(playerHandle, 200);

                    //设置硬解码
                    libPlayer.SetSmartPlayerVideoHWDecoder(playerHandle,1);
                    //静音
                    libPlayer.SmartPlayerSetMute(playerHandle,1);

					if(playbackUrl == null){
						Log.e(TAG, "playback URL with NULL...");
						return;
					}

					int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);

					if(iPlaybackRet != 0)
					{
						Log.e(TAG, "StartPlayback strem failed..");
						return;
					}

					btnStartStopPlayback.setText("停止播放 ");
					//btnPopInputText.setEnabled(false);
					isPlaybackViewStarted = true;
					Log.i(TAG, "Start playback stream--");
				}
			}
		});
	}


	class EventHande implements SmartEventCallback
	{
		@Override
		public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5){
			switch (code) {
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STARTED:
					Log.i(TAG, "开始。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTING:
					Log.i(TAG, "连接中。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTION_FAILED:
					Log.i(TAG, "连接失败。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTED:
					Log.i(TAG, "连接成功。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_DISCONNECTED:
					Log.i(TAG, "连接断开。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STOP:
					Log.i(TAG, "关闭。。");
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_RESOLUTION_INFO:
					Log.i(TAG, "分辨率信息: width: " + param1 + ", height: " + param2);
					break;
				case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_NO_MEDIADATA_RECEIVED:
					Log.i(TAG, "收不到媒体数据，可能是url错误。。");
			}
		}
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
		Log.i(getClass().getName(),
				"onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
						+ e2.getX() + "," + e2.getY() + ")");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//SettingActivity.strMessage = "hello spt\n";
		//new Thread(SettingActivity.sendThread).start();
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


	/* Create rendering */
	private boolean CreateView() {

		if(sSurfaceView == null)
		{
        	 /*
             *  useOpenGLES2:
             *  If with true: Check if system supports openGLES, if supported, it will choose openGLES.
             *  If with false: it will set with default surfaceView;	
             */
			sSurfaceView = NTRenderer.CreateRenderer(this, true);
		}

		if(sSurfaceView == null)
		{
			Log.i(TAG, "Create render failed..");
			return false;
		}

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.i(TAG, "Run into onConfigurationChanged++");

		if (null != fFrameLayout)
		{
			fFrameLayout.removeAllViews();
			fFrameLayout = null;
		}

		if (null != lLayout)
		{
			lLayout.removeAllViews();
			lLayout = null;
		}

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Log.i(TAG, "onConfigurationChanged, with LANDSCAPE。。");

			inflateLayout(LinearLayout.HORIZONTAL);

			currentOrigentation = LANDSCAPE;
		}
		else
		{
			Log.i(TAG, "onConfigurationChanged, with PORTRAIT。。");

			inflateLayout(LinearLayout.VERTICAL);

			currentOrigentation = PORTRAIT;
		}

		if(!isPlaybackViewStarted)
			return;

		libPlayer.SmartPlayerSetOrientation(playerHandle, currentOrigentation);

		Log.i(TAG, "Run out of onConfigurationChanged--");
	}

	@Override
	protected  void onDestroy()
	{
		Log.i(TAG, "Run into activity destory++");

		play_mode = 0;
		SettingActivity.strMessage = String.valueOf(play_mode)+ ":0:0";
		new Thread(SettingActivity.sendThread).start();

		if(playerHandle!=0)
		{
			libPlayer.SmartPlayerClose(playerHandle);
			playerHandle = 0;
		}
		super.onDestroy();
		//finish();
		//System.exit(0);
	}
}