package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.share.ShareApi;
//import com.facebook.share.Sharer;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.model.SharePhoto;
//import com.facebook.share.model.SharePhotoContent;
//import com.facebook.share.widget.ShareButton;
//import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.HiitSettingActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.ExpandableListAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;

public class ProgressActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private LineChart mLineChart;
    private int index = -1;

    private TextView mDateTextView, mNoResultTextView;
    private ArrayList<WorkoutLog> logList;
    private ExpandableListView expandableLv;
    private ExpandableListAdapter mAdapter;

    private View footerView;
    private TextView mTotalWorkoutTimeTextView, mTotalRestTime, mTotalTimeTextView;

    private SimpleDateFormat simpleDateFormat, detailDateFormat;

    private Realm logRealm;
    private Calendar c = Calendar.getInstance();
    private int count;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private WorkoutLog selectedWorkoutLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        count = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ProgressActivity.this).getString(getResources().getString(R.string.SHARED_PREF_CHART_VIEW_KEY), "5"));

        Toolbar hiitTimerListToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerListToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        hiitTimerListToolbar.setTitle(getResources().getString(R.string.progress_toolbar));
        setSupportActionBar(hiitTimerListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logRealm = Realm.getDefaultInstance();

        initializeViews();
        initializeChart();
        initializeDataset();
    }

    private void initializeChart() {

        mLineChart.setViewPortOffsets(70, 0, 40, 60);
        mLineChart.setDescription(getResources().getString(R.string.progress_x_label_desc));
        mLineChart.setDescriptionColor(Color.WHITE);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(false);
        mLineChart.setDrawGridBackground(false);

        XAxis x = mLineChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setSpaceBetweenLabels(0);
        x.setTextColor(Color.WHITE);
        x.setAvoidFirstLastClipping(true);
        x.setDrawGridLines(true);
        x.setGridColor(Color.WHITE);

        YAxis y = mLineChart.getAxisLeft();
        //y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(true);
        y.setGridColor(Color.WHITE);
        y.setAxisLineColor(Color.WHITE);

        mLineChart.getAxisRight().setEnabled(false);

        // add data
        setData(count);

        mLineChart.getLegend().setEnabled(false);
        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.animateXY(0, 2000);
        mLineChart.invalidate();
    }

    private void initializeDataset() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        mAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableLv.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (footerView.getVisibility() == View.VISIBLE) {
            expandableLv.removeFooterView(footerView);
        }

        if (logList.get(e.getXIndex()) == null) {
            c.setTime(new Date());
            c.add(Calendar.DATE, e.getXIndex() - (logList.size() - 1));

            clearDataset();

            mDateTextView.setText(detailDateFormat.format(c.getTime()));
            mNoResultTextView.setVisibility(View.VISIBLE);
            mNoResultTextView.setText(getResources().getString(R.string.progress_no_workout_data));
        } else {
            selectedWorkoutLog = logList.get(e.getXIndex());
            Date date = selectedWorkoutLog.getmDate();
            mDateTextView.setText(detailDateFormat.format(date));

            if (selectedWorkoutLog.getTimerLogs().size() == 1 && selectedWorkoutLog.getTimerLogs().get(0).getTotal() == 0) {
                clearDataset();
                mNoResultTextView.setVisibility(View.VISIBLE);
                mNoResultTextView.setText(getResources().getString(R.string.progress_no_workout_data));

            } else {

                Log.d("SELECTED VAL", e.getVal()+"");

                expandableLv.addFooterView(footerView);

                mNoResultTextView.setVisibility(View.GONE);
                footerView.setVisibility(View.VISIBLE);

                String totalWorkoutNames = "";
                String totalWorkTime = "";
                String totalRestTime = "";
                for (int i = 0; i < selectedWorkoutLog.getTimerLogs().size(); i++) {
                    if (i != selectedWorkoutLog.getTimerLogs().size()) {
                        totalWorkoutNames += selectedWorkoutLog.getTimerLogs().get(i).getWorkoutNames() + "=";
                        totalWorkTime += selectedWorkoutLog.getTimerLogs().get(i).getWorkSecs() + "=";
                        totalRestTime += selectedWorkoutLog.getTimerLogs().get(i).getRestSecs() + "=";
                    } else {
                        totalWorkoutNames += selectedWorkoutLog.getTimerLogs().get(i).getWorkoutNames();
                        totalWorkTime += selectedWorkoutLog.getTimerLogs().get(i).getWorkSecs();
                        totalRestTime += selectedWorkoutLog.getTimerLogs().get(i).getRestSecs();
                    }
                }
                String[] totalWorkoutNameArray = totalWorkoutNames.split("=");
                String[] totalWorkTimeArray = totalWorkTime.split("=");
                String[] totalRestTimeArray = totalRestTime.split("=");

                String[] sortedWorkoutNameArray = Arrays.copyOf(totalWorkoutNameArray, totalWorkoutNameArray.length);
                Arrays.sort(sortedWorkoutNameArray);
                listDataHeader.clear();
                listDataHeader.add(sortedWorkoutNameArray[0]);
                for (int i = 0; i < sortedWorkoutNameArray.length - 1; i++) {
                    if (!sortedWorkoutNameArray[i].equals(sortedWorkoutNameArray[i + 1])) {
                        listDataHeader.add(sortedWorkoutNameArray[i + 1]);
                    }
                }

                int todaysTotalTime = 0;

                for (int i = 0; i < listDataHeader.size(); i++) {
                    ArrayList<String> eachWorkTimes = new ArrayList<>();
                    for (int j = 0; j < totalWorkoutNameArray.length; j++) {
                        if (listDataHeader.get(i).equals(totalWorkoutNameArray[j])) {
                            eachWorkTimes.add(totalWorkTimeArray[j]);
                        }
                    }
                    Collections.sort(eachWorkTimes);
                    HashMap<String, Integer> timesWithReps = new HashMap<>();
                    int mapIndex = 0;
                    timesWithReps.put(eachWorkTimes.get(mapIndex), 1);
                    for (int k = 0; k < eachWorkTimes.size() - 1; k++) {
                        if (eachWorkTimes.get(k).equals(eachWorkTimes.get(k + 1))) {
                            timesWithReps.put(eachWorkTimes.get(mapIndex), timesWithReps.get(eachWorkTimes.get(mapIndex)) + 1);
                        } else {
                            mapIndex = k + 1;
                            timesWithReps.put(eachWorkTimes.get(mapIndex), 1);
                        }
                    }
                    int eachTotalTime = 0;
                    List<String> childList = new ArrayList<>();
                    for (String key : timesWithReps.keySet()) {
                        String convertedTime = TimerUtils.convertRawSecIntoString(Integer.parseInt(key));
                        String newChild = convertedTime + " x " + timesWithReps.get(key) + " Sets = " + TimerUtils.convertRawSecIntoString(Integer.parseInt(key) * timesWithReps.get(key));
                        if (timesWithReps.get(key) == 1) {
                            newChild = convertedTime + " x " + timesWithReps.get(key) + " Set = " + TimerUtils.convertRawSecIntoString(Integer.parseInt(key) * timesWithReps.get(key));
                        }
                        Log.d("NEW CHILD", newChild);
                        childList.add(newChild);
                        eachTotalTime += Integer.parseInt(key) * timesWithReps.get(key);
                    }
                    listDataChild.put(listDataHeader.get(i), childList);
                    todaysTotalTime += eachTotalTime;
                }

                mTotalWorkoutTimeTextView.setText(TimerUtils.convertRawSecIntoString(todaysTotalTime));
                int totalTime = 0;
                int totalRest = 0;
                for (int i = 0; i < selectedWorkoutLog.getTimerLogs().size(); i++) {
                    totalTime += selectedWorkoutLog.getTimerLogs().get(i).getTotal();
                }
                for (String eachRestTime : totalRestTimeArray) {
                    totalRest += Integer.parseInt(eachRestTime);
                }
                mTotalRestTime.setText(TimerUtils.convertRawSecIntoString(totalRest));
                mTotalTimeTextView.setText(TimerUtils.convertRawSecIntoString(totalTime));

                mAdapter.notifyDataSetChanged();



            }
        }
    }

    @Override
    public void onNothingSelected() {
    }

    private void clearDataset() {
        listDataHeader.clear();
        listDataChild.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void setData(int count) {

        RealmResults<WorkoutLog> results = logRealm.where(WorkoutLog.class).findAll();
        for (int i = 0; i < results.size(); i++) {
            Log.d("FOUND DATA", "Date : " + results.get(i).getmDate() +
                    ", Workout details : " + results.get(i).getTimerLogs().toString());
        }
        if (results.size() == 0) {
            return;
        } else {
            logList = new ArrayList<>();
            for (int i = 0; i < count - results.size(); i++) {
                logList.add(null);
            }
            if (results.size() > count){
                for (int i = results.size() - count; i < results.size(); i++) {
                    logList.add(copyObjectsFromDB(results, i));
                }
            } else {
                for (int i = 0; i < results.size(); i++) {
                    logList.add(copyObjectsFromDB(results, i));
                }
            }
            Log.d("LOGLIST_SIZE", logList.size() + "");
            Date today = new Date();
            String todayString = simpleDateFormat.format(today);

            if (!simpleDateFormat.format(logList.get(logList.size()-1).getmDate()).equals(todayString)){
                logList.remove(0);
                logList.add(null);
            }

                ArrayList<String> xVals = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (logList.get(i) != null) {
                    xVals.add(simpleDateFormat.format(logList.get(i).getmDate()));
                } else {
                    c.setTime(today);
                    c.add(Calendar.DATE, i - (count - 1));
                    xVals.add(simpleDateFormat.format(c.getTime()));

                }
            }
            today = null;
            for (int i = 0; i < xVals.size(); i++) {
                Log.d(getClass().getSimpleName(), xVals.get(i));
            }

            ArrayList<Entry> vals1 = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (i < logList.size()) {
                    vals1.add(getYVals(logList.get(i)));
                } else {
                    vals1.add(getYVals(null));
                }
            }

            LineDataSet set1 = new LineDataSet(vals1, "Total workout time");
            set1.setDrawCubic(true);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.WHITE);
            set1.setHighlightLineWidth(2.0f);
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            LineData data = new LineData(xVals, set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            mLineChart.setData(data);
        }
    }

    private WorkoutLog copyObjectsFromDB(RealmResults<WorkoutLog> logs, int position) {
        if (logs.size() <= position) {
            return null;
        } else {
            return logs.get(position);
        }
    }

    private Entry getYVals(WorkoutLog workoutLog) {
        long totalWorkoutTimeOfTheDay = 0L;
        index += 1;
        if (workoutLog != null) {
            for (int i = 0; i < workoutLog.getTimerLogs().size(); i++) {
                totalWorkoutTimeOfTheDay += (workoutLog.getTimerLogs().get(i).getTotal());
            }
            return new Entry(totalWorkoutTimeOfTheDay/60, index);
        } else {
            return new Entry(0, index);
        }
    }

    private void initializeViews() {

        mDateTextView = new TextView(this);
        mDateTextView.setPadding(16, 16, 16, 16);
        mDateTextView.setTypeface(null, Typeface.BOLD);
        mDateTextView.setText("");

        mNoResultTextView = (TextView) findViewById(R.id.no_result_text_view);
        footerView = LayoutInflater.from(this).inflate(R.layout.footer_view, expandableLv, false);
        footerView.setVisibility(View.GONE);
        mTotalWorkoutTimeTextView = (TextView) footerView.findViewById(R.id.progress_detail_total_workout_time_text_view);
        mTotalTimeTextView = (TextView) footerView.findViewById(R.id.progress_detail_total_time_text_view);
        mTotalRestTime = (TextView) footerView.findViewById(R.id.progress_detail_total_rest_time_text_view);

        expandableLv = (ExpandableListView) findViewById(R.id.progress_expandable_list_view);
        mLineChart = (LineChart) LayoutInflater.from(this).inflate(R.layout.line_chart_header, expandableLv, false);
        expandableLv.addHeaderView(mLineChart);
        expandableLv.addHeaderView(mDateTextView);

        simpleDateFormat = new SimpleDateFormat("MMM dd");
        detailDateFormat = new SimpleDateFormat("MMMM dd, EEEE");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.progress_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.progress_menu_share_icon:
                Bitmap bitmap = mLineChart.getChartBitmap();
                File file = new File(ProgressActivity.this.getCacheDir(), Calendar.getInstance().getTimeInMillis() + ".jpeg");
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareIntent.setType("image/png");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logRealm.close();
    }
}
