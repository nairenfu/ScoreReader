package com.hylux.scorereader;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

public class ProgressBars extends Fragment {

    private ProgressBar timeToScroll;

    private ObjectAnimator animator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_progress_bars, container, false);

        timeToScroll = (ProgressBar) view.findViewById(R.id.timeToScroll);
        Log.d("timeToScroll", String.valueOf(timeToScroll));
        animator = ObjectAnimator.ofInt(timeToScroll, "progress", 0, 100);

        return view;
    }

    public void update(int time) {
        animator.setDuration(time);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        Log.d("Update", "updating");
        Log.d("allFragments", String.valueOf(getActivity().getSupportFragmentManager().getFragments()));
    }

    public void pause() {
        animator.pause();
    }

    public void resume() {
        animator.resume();
    }

}
