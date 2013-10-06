/*);
 * Copyright (C) 2013 Vanir AOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2.vanir;

import com.android.launcher.R;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.InputType;

public class VanirDreamSettings extends PreferenceActivity
    implements OnPreferenceChangeListener {

    public static final String INTERACTIVE = "vanir_dream_interactive";
    public static final String ROTATION = "vanir_dream_rotation";
    public static final String COUNT = "vanir_dream_count";
    public static final String DEBUG = "vanir_dream_debug";
    public static final int DEFAULT_NUM_MOB = 15;
    public static final int MAX_NUM_MOB = 50;

    private CheckBoxPreference mInteractivePref, mRotationPref, mDebugModePref;
    private EditTextPreference mCountPref;
    
    private boolean mIsInteractiveEnabled, mIsRotationEnabled, mDebugMode;
    private int mNumberOfMobs;
    private static String mMaxMobSize;
    private static String mCurrentMobSize;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.vanir_dream_settings);
        
        mContext = (Context) this;
        mMaxMobSize = getString(R.string.vanir_dream_mob_max_count_warning);
        mCurrentMobSize = getString(R.string.vanir_dream_current_mob_size);

        mInteractivePref = (CheckBoxPreference) findPreference(INTERACTIVE);
        mInteractivePref.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                INTERACTIVE, 1) == 1 ? true : false);
        mInteractivePref.setOnPreferenceChangeListener(this);

        mRotationPref = (CheckBoxPreference) findPreference(ROTATION);
        mRotationPref.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                ROTATION, 1) == 1 ? true : false);
        mRotationPref.setOnPreferenceChangeListener(this);
        
        mCountPref = (EditTextPreference)findPreference(COUNT);
        mCountPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        String value = String.valueOf(Settings.System.getInt(mContext.getContentResolver(),
                COUNT, DEFAULT_NUM_MOB));

        mCountPref.setDefaultValue(value);
        mCountPref.setText(value);
        updateCountSummary(value);
        mCountPref.setOnPreferenceChangeListener(this);
        
        mDebugModePref = (CheckBoxPreference) findPreference(DEBUG);
        mDebugModePref.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                DEBUG, 0) == 1 ? true : false);
        mDebugModePref.setOnPreferenceChangeListener(this);        
    }

    private void updateCountSummary(String value) {
        StringBuilder b = new StringBuilder();
        b.append(mMaxMobSize)
                .append(" ")
                .append(String.valueOf(MAX_NUM_MOB))
                .append("\n")
                .append(mCurrentMobSize)
                .append(" ")
                .append(value);
        mCountPref.setSummary(b.toString());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mInteractivePref)) {
            mIsInteractiveEnabled = (Boolean)newValue;
            Settings.System.putInt(mContext.getContentResolver(), INTERACTIVE, mIsInteractiveEnabled ? 1 : 0);
            VanirDream.putInteractive(mIsInteractiveEnabled);
            return true;
        } else if (preference.equals(mRotationPref)) {
            mIsRotationEnabled = (Boolean)newValue;
            Settings.System.putInt(mContext.getContentResolver(), ROTATION, mIsRotationEnabled ? 1 : 0);
            VanirDreamEngine.putRotation(mIsRotationEnabled);
            return true;
        } else if (preference.equals(mCountPref)) {
            mNumberOfMobs = (Integer.parseInt((String) newValue));
            if (mNumberOfMobs > MAX_NUM_MOB) {
                return false;
            } else {
                Settings.System.putInt(mContext.getContentResolver(), COUNT, mNumberOfMobs);
                VanirDreamEngine.putMobs(mNumberOfMobs);
                updateCountSummary(String.valueOf(mNumberOfMobs));
                return true;
            }
        } else if (preference.equals(mDebugModePref)) {
            mDebugMode = (Boolean)newValue;
            Settings.System.putInt(mContext.getContentResolver(), DEBUG, mDebugMode ? 1 : 0);
            VanirDreamEngine.putDebugMode(mDebugMode);
            return true;
        }
        return false;
    }
}
