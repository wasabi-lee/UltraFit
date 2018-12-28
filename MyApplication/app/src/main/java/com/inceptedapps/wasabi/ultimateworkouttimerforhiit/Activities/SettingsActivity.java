package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.GoPremiumActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnThemeChangedListener {

    Toolbar mSettingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);

        Log.d(getClass().getSimpleName(), "onCreate");
        initializeToolbar();
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.general_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.go_premium_menu:
                Intent intent = new Intent(SettingsActivity.this, GoPremiumActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeToolbar() {
        mSettingToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        mSettingToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    @Override
    public void onThemeChanged(int theme) {
        ThemeUtils.changeToTheme(theme);
        recreate();
    }
}


