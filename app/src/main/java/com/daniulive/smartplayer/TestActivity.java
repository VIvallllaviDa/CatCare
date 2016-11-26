package com.daniulive.smartplayer;

import android.os.Bundle;
import android.widget.Toast;

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

@AILayout(R.layout.activity_test)
public class TestActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

        @AIView(R.id.activity_test_rfal)
        private RapidFloatingActionLayout rfaLayout;
        @AIView(R.id.activity_test_rfab)
        private RapidFloatingActionButton rfaBtn;
        private RapidFloatingActionHelper rfabHelper;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_test);


            RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
            rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
            List<RFACLabelItem> items = new ArrayList<>();
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("毛线球")
                    .setResId(R.drawable.maoxianqiu)
                    .setIconNormalColor(0xff7cb388)
                    .setIconPressedColor(0xffbf360c)
                    .setLabelColor(0xff652b14)
                    .setWrapper(0)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("鱼")
                    .setResId(R.drawable.yu)
                    .setIconNormalColor(0xff7cb388)
                    .setIconPressedColor(0xffbf360c)
                    .setLabelColor(0xff652b14)
                    .setWrapper(1)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("老鼠")
                    .setResId(R.drawable.laoshu)
                    .setIconNormalColor(0xff7cb388)
                    .setIconPressedColor(0xffbf360c)
                    .setLabelColor(0xff652b14)
                    .setWrapper(2)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("手动")
                    .setResId(R.drawable.shoudong)
                    .setIconNormalColor(0xff7cb388)
                    .setIconPressedColor(0xffbf360c)
                    .setLabelColor(0xff652b14)
                    .setWrapper(3)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("暂停")
                    .setResId(R.drawable.zanting)
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

        }

        @Override
        public void onRFACItemLabelClick(int position, RFACLabelItem item) {
            Toast.makeText(getContext(), "clicked label: " + position, Toast.LENGTH_SHORT).show();
            rfabHelper.toggleContent();
        }

        @Override
        public void onRFACItemIconClick(int position, RFACLabelItem item) {
            Toast.makeText(getContext(), "clicked icon: " + position, Toast.LENGTH_SHORT).show();
            rfabHelper.toggleContent();
        }
}
