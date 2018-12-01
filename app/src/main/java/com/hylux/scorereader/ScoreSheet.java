package com.hylux.scorereader;

import android.Manifest;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import com.shockwave.pdfium.PdfDocument;

import java.io.File;

public class ScoreSheet extends Fragment implements OnPageChangeListener, OnLoadCompleteListener {

    private ScoreView scoreViewActivity;
    private PDFView score;
    private ProgressBars progressBars;

    Handler handler = new Handler();
    Runnable runnable;

    private boolean isLandscape;
    private boolean isRunning = false;
    private int run = 0;

    private float ratio = 29.7f / 21f;
    private float phoneHeight, phoneWidth;
    private float scrollSpeed = -phoneHeight * ratio / 7;
    private int startCount = 1; //1 row before scrolling starts
    private int delay = 5000;

    private int pageNumber = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdfview, container, false);

        checkPermissions();
        Log.d("Perms", "Permission Granted");

        scoreViewActivity = (ScoreView) getActivity();
        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        score = (PDFView) view.findViewById(R.id.score);
        progressBars = (ProgressBars) getActivity().getSupportFragmentManager().findFragmentById(R.id.progressBars);
        Log.d("progressBars", String.valueOf(progressBars));
        Log.d("allFragments", String.valueOf(getActivity().getSupportFragmentManager().getFragments()));


        phoneHeight = scoreViewActivity.getIntent().getExtras().getFloat("phoneHeight");
        Log.d("Height", String.valueOf(phoneHeight));
        phoneWidth = scoreViewActivity.getIntent().getExtras().getFloat("phoneWidth");
        Log.d("Width", String.valueOf(phoneWidth));
        score.setMaxZoom(1.2f * phoneHeight / phoneWidth);
        Log.d("Ratio", String.valueOf(score.getMaxZoom()));
        delay = scoreViewActivity.getIntent().getExtras().getInt("delay");
        scrollSpeed = -phoneHeight * ratio / (scoreViewActivity.getIntent().getExtras().getFloat("rowsPerPage") + 1);
        startCount = scoreViewActivity.getIntent().getExtras().getInt("startDelay");
        Log.d("ScrollSpeed", String.valueOf(scrollSpeed));

        File file = new File(scoreViewActivity.getIntent().getExtras().getString("path")).getAbsoluteFile();
        Uri path = Uri.fromFile(file);
        Log.d("Path", path.toString());
        displayUri(path);

        score.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                Log.d("Click", "click");
                Log.d("isRunning", String.valueOf(isRunning));
                progressBars = (ProgressBars) getActivity().getSupportFragmentManager().findFragmentById(R.id.progressBars);
                Log.d("progressBars", String.valueOf(progressBars));
                Log.d("allFragments", String.valueOf(getActivity().getSupportFragmentManager().getFragments()));
            }
        });

        if (isLandscape) {
            //Do some stuff
            Log.d("Orientation", "Landscape");

            Log.d("Max zoom", String.valueOf(score.getMaxZoom()));
            score.zoomTo(score.getMaxZoom());
            score.loadPages();
        }

        return view;
    }

    public void onStart() {
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("Run", String.valueOf(run));
                Log.d("StartCount", String.valueOf(startCount));
                Log.d("isRunning", String.valueOf(isRunning));

                if (startCount <= 0 && isRunning) {
                    try {
                        progressBars.update(delay);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    score.moveRelativeTo(0, scrollSpeed);
                    Log.d("Scroll", "Scroll");
                    score.loadPages();
                } else if (startCount > 0) startCount -= 1;

                if (startCount <= 0 && !isRunning) {
                    try {
                        progressBars.pause();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                run += 1;
                Log.d("isRunning", String.valueOf(isRunning));
                Log.d("StartCount", String.valueOf(startCount));
                Log.d("Opt width", String.valueOf(score.getOptimalPageWidth()));
                Log.d("Height", String.valueOf(score.getHeight()));

                runnable = this;
                handler.postDelayed(runnable, delay);
            }
        }, delay);
        super.onStart();
    }

    public void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }

    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT > 22) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 42);
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = score.getDocumentMeta();

    }

    public void displayUri(Uri path) {
        Log.d("NPE", String.valueOf(path));
        score.fromUri(path).defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .load();
        score.setClickable(true);
        isRunning = false;
        Log.d("load", "Loaded");
        Log.d("Clickable", String.valueOf(score.isClickable()));
    }

}