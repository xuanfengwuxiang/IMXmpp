package com.heima.im.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {
    private View view =null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (view == null){
            view = createView(inflater,container,savedInstanceState);

        }else{
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent!=null){
                parent.removeView(view);
            }
        }
        return view;
    }

    protected abstract  View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) ;
}
