package com.hylux.scorereader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private String path;

    DisplayMetrics displaymetrics = new DisplayMetrics();
    private float phoneHeight, phoneWidth;

    private int startDelay = 1;
    private int delay = 5000;
    private float rowsPerPage = 7;

    private String help;
    private Fragment info;
    private FragmentTransaction fragmentTransaction;
    TextView fileShown;
    TextView showHelp;
    InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        phoneHeight = displaymetrics.heightPixels;
        phoneWidth = displaymetrics.widthPixels;

        final EditText filePath = (EditText) findViewById(R.id.fileSelect);
        final EditText setStartDelay = (EditText) findViewById(R.id.startDelay);
        final EditText setDelay = (EditText) findViewById(R.id.delay);
        final EditText setRowsPerPage = (EditText) findViewById(R.id.scrollSpeed);
        final Button fileChosen = (Button) findViewById(R.id.fileSelectChosen);

        info = getSupportFragmentManager().findFragmentById(R.id.infoPage);
        fileShown = (TextView) findViewById(R.id.showFile);
        showHelp = (TextView) findViewById(R.id.showHelp);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(info).commit();

        fileChosen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (getFileExt(filePath.getText().toString()).equalsIgnoreCase("pdf")) {
                    setPath(filePath.getText().toString());
                } else {
                    setPath("Please enter a PDF");
                }
                Log.d(filePath.getText().toString(), getFileExt(filePath.getText().toString()));
                fileShown.setText(path);

                try {
                    setDelay(Integer.parseInt(setDelay.getText().toString()));
                    Log.d("Delay", setDelay.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    setRowsPerPage(Integer.parseInt(setRowsPerPage.getText().toString()));
                    Log.d("rowsPerPage", setRowsPerPage.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    setStartDelay(Integer.parseInt(setStartDelay.getText().toString()));
                    Log.d("startDelay", setStartDelay.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    private void setDelay(int delay) {
        this.delay = delay;

    }

    public Activity getActivity() {
        return this;
    }

    private void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    private void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public void viewScore(View view) {
        Log.d("rowsPerPage", String.valueOf(rowsPerPage));
        Intent intent = new Intent(this, ScoreView.class);

        intent.putExtra("phoneHeight", phoneHeight);
        Log.d("Height", String.valueOf(phoneHeight));
        intent.putExtra("phoneWidth", phoneWidth);
        Log.d("Width", String.valueOf(phoneWidth));

        Log.d("Path", String.valueOf(this.path));

        try {
            intent.putExtra("delay", this.delay);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            intent.putExtra("rowsPerPage", this.rowsPerPage);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            intent.putExtra("startDelay", this.startDelay);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (path != null) {
            if (new File(path).getAbsoluteFile().exists()) {
                intent.putExtra("path", this.path);
                MainActivity.this.startActivity(intent);
            } else {
                fileShown.setText("Please enter a vaild PDF");
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else {
            fileShown.setText("Please select a PDF");
        }
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public void showInfo(View view) {

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //getSupportFragmentManager().beginTransaction().add(R.id.activity_main, new Info(), "infoPage").commit();
        Log.d("showInfo", "True");
        Log.d("allFragments", String.valueOf(getSupportFragmentManager().getFragments()));
        if (!info.isHidden()) fragmentTransaction.hide(info).commit();
        if (info.isHidden()) fragmentTransaction.show(info).commit();
        //Log.d("Is info hidden", String.valueOf(getSupportFragmentManager().findFragmentById(R.id.infoPage).isHidden()));
    }

    private void setHelp(String help) {
        this.help = help;
    }

    public void helpDelay(View view) {
        setHelp("This is the amount of time (ms) between a scroll and the next. (Default = 5000 ms)");
        showHelp.setText(help);
    }

    public void helpRows(View view) {
        setHelp("This is the average number of rows of bars per page. (Default = 6 rows)");
        showHelp.setText(help);
    }

    public void helpStartDelay(View view) {
        setHelp("This is the number of rows * amount of time worth of delay befor starting.");
        showHelp.setText(help);
    }
}
