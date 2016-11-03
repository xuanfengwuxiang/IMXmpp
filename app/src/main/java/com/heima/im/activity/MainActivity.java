package com.heima.im.activity;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heima.im.R;
import com.heima.im.fragment.ContactFragment;
import com.heima.im.fragment.SessionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends FragmentActivity {

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.session)
    TextView session;
    @InjectView(R.id.contact)
    TextView contact;
    @InjectView(R.id.tablayout)
    LinearLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        title.setText("会话");
        session.setEnabled(false);
        session.setOnClickListener(new click());
        contact.setOnClickListener(new click());
        final List<Fragment> pages = new ArrayList<>();
        pages.add(new SessionFragment());
        pages.add(new ContactFragment());
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    title.setText("会话");
                    session.setEnabled(false);
                    contact.setEnabled(true);

                }else if(position==01){
                    title.setText("好友");
                    session.setEnabled(true);
                    contact.setEnabled(false);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    class click implements OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.session:
                    viewpager.setCurrentItem(0);
                    break;
                case R.id.contact:
                    viewpager.setCurrentItem(1);
                    break;
            }

        }
    }
}
