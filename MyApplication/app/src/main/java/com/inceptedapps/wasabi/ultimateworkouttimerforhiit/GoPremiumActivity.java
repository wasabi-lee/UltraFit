package com.inceptedapps.wasabi.ultimateworkouttimerforhiit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.MainActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.IsPremiumSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabResult;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.Purchase;

public class GoPremiumActivity extends AppCompatActivity {

    private static final String TAG = GoPremiumActivity.class.getSimpleName();

    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    static final String PREMIUM_SKU = "com.inceptedapps.ultrafit.pro";
    private SharedPreferences sharedPref;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);

        String base64EncodedPublicKey = getResources().getString(R.string.app_license_key);
        sharedPref = GoPremiumActivity.this.getSharedPreferences(getString(R.string.shared_pref_open_key), Context.MODE_PRIVATE);
        button = (Button) findViewById(R.id.go_premium_button);

        TextView mPresetTitle = (TextView) findViewById(R.id.presets_title_tv);
        TextView mPresetDesc = (TextView) findViewById(R.id.presets_desc_tv);
        TextView mAdFreeTitle = (TextView) findViewById(R.id.ad_free_title_tv);
        TextView mAdFreeDesc = (TextView) findViewById(R.id.ad_free_desc_tv);

        int theme = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        if (theme == 3 || theme == 5) {
            mPresetTitle.setTextColor(Color.WHITE);
            mPresetDesc.setTextColor(Color.WHITE);
            mAdFreeTitle.setTextColor(Color.WHITE);
            mAdFreeDesc.setTextColor(Color.WHITE);
        }

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(getClass().getSimpleName(), "In-app billing setup failed: " + result);
                } else {
                    Log.d(getClass().getSimpleName(), "In-app billing is set up OK");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(getClass().getSimpleName(), "" + sharedPref.getBoolean(MainActivity.sharedPrefPremiumKey, false));
                if (!sharedPref.getBoolean(getString(R.string.shared_pref_premium_key), false)) {
                    try {
                        mHelper.launchPurchaseFlow(GoPremiumActivity.this, PREMIUM_SKU, 100012,
                                mPurchaseFinishedListener, "");
                    } catch (Exception e) {
                        Log.d(getClass().getSimpleName(), "Exception!!");
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(GoPremiumActivity.this, "You've already done premium upgrade!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (mHelper == null) return;

                if (result.isFailure()) {
                    Log.d(getClass().getSimpleName(), result + "");
                    return;
                }

                Toast.makeText(GoPremiumActivity.this, "Thanks for upgrading to premium!", Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Payment successful");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.shared_pref_premium_key), true);
                editor.commit();

                IsPremiumSingleton.getInstance().setPremium(true);

            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data);
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
