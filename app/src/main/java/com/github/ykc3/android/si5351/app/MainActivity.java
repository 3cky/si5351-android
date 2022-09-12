/*
 * Copyright (c) 2022 Victor Antonovich <v.antonovich@gmail.com>
 *
 *  This work is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This work is distributed in the hope that it will be useful, but
 *  without any warranty; without even the implied warranty of merchantability
 *  or fitness for a particular purpose. See the GNU Lesser General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package com.github.ykc3.android.si5351.app;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.github.ykc3.android.si5351.Si5351;
import static com.github.ykc3.android.si5351.Si5351.SI5351_CRYSTAL_LOAD_8PF;
import static com.github.ykc3.android.si5351.Si5351.SI5351_FREQ_MULT;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.*;
import static com.github.ykc3.android.si5351.Si5351.si5351_pll_input.SI5351_PLL_INPUT_XO;

import com.github.ykc3.android.usbi2c.UsbI2cAdapter;
import com.github.ykc3.android.usbi2c.UsbI2cManager;
import com.github.ykc3.android.widget.decimalnumberpicker.DecimalNumberPicker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION =
            "com.github.ykc3.android.si5351.app.USB_PERMISSION";

    public static final String SI5351_PREFS_CLOCK_OUTPUT_STATE = "SI5351_CLOCK_OUTPUT_STATE";
    public static final String SI5351_PREFS_CLOCK_FREQUENCY = "SI5351_CLOCK_FREQUENCY";
    public static final String SI5351_PREFS_CLOCK_OUTPUT_DRIVE_STRENGTH =
            "SI5351_CLOCK_OUTPUT_DRIVE_STRENGTH";
    public static final String SI5351_PREFS_FREQUENCY_CORRECTION_PPM =
            "SI5351_FREQUENCY_CORRECTION_PPM";

    private UsbManager usbManager;

    private UsbI2cManager usbI2cManager;

    private UsbI2cAdapter i2cAdapter;

    private PendingIntent usbPermissionIntent;

    private TextView statusView;

    private final HandlerThread si5351HandlerThread = new HandlerThread("Si5351 Handler Thread");
    private Handler si5351Handler;

    private ViewGroup si5351ControlsGroup;

    private DecimalNumberPicker freqCorrectionPicker;

    private final static long SI5351_STATUS_UPDATE_DELAY = 500L;

    private CheckBox pllALockedCheckBox;
    private CheckBox pllBLockedCheckBox;

    private final Map<si5351_clock, Si5351ClockControls> si5351ClockControlsMap = new HashMap<>();

    private Si5351 si5351;

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received USB device event: " + intent);
            String action = intent.getAction();
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                boolean isGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                onUsbDevicePermission(usbDevice, isGranted);
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                onUsbDeviceChanged(usbDevice, true);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                onUsbDeviceChanged(usbDevice, false);
            }
        }
    };

    private static final class Si5351ClockControls {
        private final SwitchCompat outputStateSwitch;
        private final DecimalNumberPicker frequencyPicker;
        private final Spinner outputDriveStrengthSpinner;

        private Si5351ClockControls(SwitchCompat outputStateSwitch,
                                    DecimalNumberPicker frequencyPicker,
                                    Spinner outputDriveStrengthSpinner) {
            this.outputStateSwitch = outputStateSwitch;
            this.frequencyPicker = frequencyPicker;
            this.outputDriveStrengthSpinner = outputDriveStrengthSpinner;
        }

        SwitchCompat getOutputStateSwitch() {
            return outputStateSwitch;
        }

        DecimalNumberPicker getFrequencyPicker() {
            return frequencyPicker;
        }

        Spinner getOutputDriveStrengthSpinner() {
            return outputDriveStrengthSpinner;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Create activity");

        setContentView(R.layout.main);

        si5351HandlerThread.start();
        si5351Handler = new Handler(si5351HandlerThread.getLooper());

        statusView = findViewById(R.id.status_text);

        si5351ControlsGroup = findViewById(R.id.controls_group);

        si5351InitControls();

        // Register USB permission intent result receiver
        usbPermissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter usbReceiverFilter = new IntentFilter(ACTION_USB_PERMISSION);
        usbReceiverFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbReceiverFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, usbReceiverFilter);

        // Get Android UsbManager
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        assert usbManager != null;

        // Get USB I2C manager
        usbI2cManager = UsbI2cManager.create(usbManager).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume activity");
        si5351RestoreControls();
        checkAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause activity");
        si5351SaveControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Stop activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy activity");
        si5351HandlerThread.quitSafely();
        unregisterReceiver(usbReceiver);
        closeAdapter();
    }

    private synchronized void onUsbDeviceChanged(UsbDevice usbDevice, boolean isAttached) {
        if (isAttached) {
            checkAdapter();
        } else if (i2cAdapter != null && i2cAdapter.getUsbDevice().equals(usbDevice)) {
            closeAdapter();
        }
    }

    private synchronized void onUsbDevicePermission(UsbDevice usbDevice, boolean isGranted) {
        if (i2cAdapter == null || !i2cAdapter.getUsbDevice().equals(usbDevice)) {
            return;
        }
        if (isGranted) {
            initAdapter();
        } else {
            Log.d(TAG, "Permission denied for device: " + usbDevice);
            setStatus(R.string.adapter_permission_denied);
            i2cAdapter = null;
        }
    }

    private void setStatus(int statusResId) {
        statusView.setText(getString(statusResId));
    }

    private static void enableViewsRecursive(ViewGroup viewGroup, boolean isEnabled) {
        if (viewGroup == null) {
            return;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(isEnabled);
            if (child instanceof ViewGroup) {
                enableViewsRecursive((ViewGroup) child, isEnabled);
            }
        }
    }

    private void si5351EnableControls(boolean isEnabled) {
        enableViewsRecursive(si5351ControlsGroup, isEnabled);
    }

    private void si5351InitControls() {
        si5351EnableControls(false);

        si5351InitClockControls(SI5351_CLK0, findViewById(R.id.clk0));
        si5351InitClockControls(SI5351_CLK1, findViewById(R.id.clk1));
        si5351InitClockControls(SI5351_CLK2, findViewById(R.id.clk2));

        freqCorrectionPicker = findViewById(R.id.freq_correction_picker);
        freqCorrectionPicker.setOnValueChangeListener((view, oldValue, newValue) ->
                si5351UpdateFrequencyCorrection(newValue));

        pllALockedCheckBox = findViewById(R.id.pll_a_locked_checkbox);
        pllBLockedCheckBox = findViewById(R.id.pll_b_locked_checkbox);
    }

    private void si5351AddClockControls(si5351_clock clock, Si5351ClockControls clockControls) {
        si5351ClockControlsMap.put(clock, clockControls);
    }

    private Si5351ClockControls si5351GetClockControls(si5351_clock clock) {
        return si5351ClockControlsMap.get(clock);
    }

    private void si5351InitClockControls(si5351_clock clock, View clockView) {
        TextView labelTextView = clockView.findViewById(R.id.label_text);
        labelTextView.setText(getString(R.string.clk_label, clock.ordinal()));
        SwitchCompat outputStateSwitch = clockView.findViewById(R.id.output_state_switch);
        DecimalNumberPicker frequencyPicker = clockView.findViewById(R.id.frequency_picker);
        Spinner outputDriveStrengthSpinner = clockView.findViewById(R.id.output_drive_strength_spinner);
        si5351AddClockControls(clock, new Si5351ClockControls(outputStateSwitch, frequencyPicker,
                outputDriveStrengthSpinner));
        outputStateSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                si5351UpdateOutputState(clock));
        frequencyPicker.setOnValueChangeListener((view, oldValue, newValue) ->
                si5351UpdateClockFrequency(clock));
        outputDriveStrengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                si5351UpdateOutputDriveStrength(clock);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                si5351UpdateOutputDriveStrength(clock);
            }
        });
    }

    private void si5351SaveControls() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        for (si5351_clock clock : si5351_clock.values()) {
            Si5351ClockControls controls = si5351GetClockControls(clock);
            if (controls == null) {
                continue;
            }
            ed.putBoolean(si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_OUTPUT_STATE),
                    controls.getOutputStateSwitch().isChecked());
            ed.putFloat(si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_FREQUENCY),
                    controls.getFrequencyPicker().getValue());
            ed.putInt(si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_OUTPUT_DRIVE_STRENGTH),
                    controls.getOutputDriveStrengthSpinner().getSelectedItemPosition());
        }
        ed.putFloat(SI5351_PREFS_FREQUENCY_CORRECTION_PPM, freqCorrectionPicker.getValue());
        ed.apply();
    }

    private void si5351RestoreControls() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        for (si5351_clock clock : si5351_clock.values()) {
            Si5351ClockControls controls = si5351GetClockControls(clock);
            if (controls == null) {
                continue;
            }
            controls.getOutputStateSwitch().setChecked(prefs.getBoolean(
                    si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_OUTPUT_STATE),
                    true));
            controls.getFrequencyPicker().setValue(prefs.getFloat(
                    si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_FREQUENCY),
                    getResources().getInteger(R.integer.si5351_initial_frequency_khz)));
            controls.getOutputDriveStrengthSpinner().setSelection(prefs.getInt(
                    si5351GetClockControlsKey(clock, SI5351_PREFS_CLOCK_OUTPUT_DRIVE_STRENGTH),
                    0));
        }

        freqCorrectionPicker.setValue(prefs.getFloat(SI5351_PREFS_FREQUENCY_CORRECTION_PPM, 0));
    }

    private static String si5351GetClockControlsKey(si5351_clock clock, String ctrls) {
        return ctrls + ":" + clock.name();
    }

    private void checkAdapter() {
        if (i2cAdapter != null) {
            return;
        }
        // Find all connected I2C adapters
        List<UsbI2cAdapter> i2cAdapters = usbI2cManager.getAdapters();
        if (i2cAdapters.isEmpty()) {
            Log.i(TAG, "No USB I2C adapters found");
            setStatus(R.string.adapter_not_found);
            return;
        }
        // Get first adapter
        i2cAdapter = i2cAdapters.get(0);
        // Check USB device access permission
        UsbDevice usbDevice = i2cAdapter.getUsbDevice();
        if (usbManager.hasPermission(usbDevice)) {
            initAdapter();
        } else {
            // Request USB device access permission
            Log.i(TAG, "Requesting USB I2C adapter access permission...");
            usbManager.requestPermission(usbDevice, usbPermissionIntent);
        }
    }

    private void openAdapter() {
        if (i2cAdapter == null) {
            return;
        }
        try {
            i2cAdapter.open();
        } catch (Exception e) {
            Log.e(TAG, "Adapter open error", e);
            setStatus(R.string.adapter_open_error);
            i2cAdapter = null;
        }
    }

    private void closeAdapter() {
        if (i2cAdapter == null) {
            return;
        }
        try {
            i2cAdapter.close();
        } catch (Exception ignored) {
        }
        i2cAdapter = null;
        si5351 = null;
        setStatus(R.string.adapter_not_found);
        si5351EnableControls(false);
    }

    private void initAdapter() {
        if (i2cAdapter == null) {
            return;
        }
        openAdapter();
        si5351CheckPresence();
    }

    private void si5351CheckPresence() {
        if (i2cAdapter == null) {
            return;
        }

        si5351 = new Si5351(i2cAdapter);

        try {
            si5351Init();
            si5351EnableControls(true);
            setStatus(R.string.si5351_found);
        } catch (IOException e) {
            Log.e(TAG, "Si5351 init error", e);
            setStatus(R.string.si5351_not_found);
        }
    }

    private void si5351Init() throws IOException {
        si5351.init(SI5351_CRYSTAL_LOAD_8PF, 0, si5351GetCrystalCorrectionPpb(
                freqCorrectionPicker.getValue()));
        si5351InitClock(SI5351_CLK0);
        si5351InitClock(SI5351_CLK1);
        si5351InitClock(SI5351_CLK2);
    }

    private static int si5351GetCrystalCorrectionPpb(float freqCorrPpm) {
        // Invert sign because increasing of effective Si5351 crystal frequency
        // leads to decreasing of the output frequency and vice versa
        return (int) (-freqCorrPpm * 1000);
    }

    private void si5351InitClock(si5351_clock clock) {
        si5351UpdateClockFrequency(clock);
        si5351UpdateOutputState(clock);
        si5351UpdateOutputDriveStrength(clock);
    }

    private void si5351UpdateFrequencyCorrection(float freqCorrPpm) {
        final Si5351 si5351 = this.si5351;
        if (si5351 == null) {
            return;
        }
        si5351Handler.post(() -> {
            int crystalCorrectionPpb = si5351GetCrystalCorrectionPpb(freqCorrPpm);
            try {
                si5351.set_correction(crystalCorrectionPpb, SI5351_PLL_INPUT_XO);
            } catch (IOException e) {
                Log.e(TAG, "Can't set Si5351 crystal correction to "
                        + crystalCorrectionPpb + " PPB", e);
            }
        });
    }

    private void si5351UpdateOutputState(si5351_clock clock) {
        final Si5351 si5351 = this.si5351;
        if (si5351 == null) {
            return;
        }
        boolean outputState = si5351GetClockControls(clock).getOutputStateSwitch().isChecked();
        si5351Handler.post(() -> {
            try {
                si5351.output_enable(clock, outputState);
            } catch (IOException e) {
                Log.e(TAG, "Can't " + (outputState ? "enable" : "disable") + " " + clock, e);
            }
        });
    }

    private void si5351UpdateOutputDriveStrength(si5351_clock clock) {
        final Si5351 si5351 = this.si5351;
        if (si5351 == null) {
            return;
        }
        int selectedItemPosition = si5351GetClockControls(clock).getOutputDriveStrengthSpinner()
                .getSelectedItemPosition();
        Si5351.si5351_drive driveStrength;
        switch (selectedItemPosition) {
            case 0:
            default:
                driveStrength = Si5351.si5351_drive.SI5351_DRIVE_2MA;
                break;
            case 1:
                driveStrength = Si5351.si5351_drive.SI5351_DRIVE_4MA;
                break;
            case 2:
                driveStrength = Si5351.si5351_drive.SI5351_DRIVE_6MA;
                break;
            case 3:
                driveStrength = Si5351.si5351_drive.SI5351_DRIVE_8MA;
                break;
        }
        si5351Handler.post(() -> {
            try {
                si5351.drive_strength(clock, driveStrength);
            } catch (IOException e) {
                Log.e(TAG, "Can't set " + clock + " output drive strength to " + driveStrength, e);
            }
        });
    }

    private void si5351UpdateClockFrequency(si5351_clock clock) {
        final Si5351 si5351 = this.si5351;
        if (si5351 == null) {
            return;
        }
        float freqKhz = si5351GetClockControls(clock).getFrequencyPicker().getValue();
        si5351Handler.post(() -> {
            try {
                si5351.set_freq((long) (freqKhz * 1000 * SI5351_FREQ_MULT), clock);
                si5351UpdateStatus();
            } catch (IOException e) {
                Log.e(TAG, "Can't set " + clock + " frequency to " + freqKhz + " kHz", e);
            }
        });
    }

    private void si5351UpdateStatus() {
        final Si5351 si5351 = this.si5351;
        if (si5351 == null) {
            return;
        }

        si5351Handler.postDelayed(() -> {
            try {
                si5351.update_status();
                runOnUiThread(() -> {
                    pllALockedCheckBox.setChecked(!si5351.dev_status.LOL_A);
                    pllBLockedCheckBox.setChecked(!si5351.dev_status.LOL_B);
                });
            } catch (Exception e) {
                Log.e(TAG, "Can't update Si5351 status", e);
            }
        }, SI5351_STATUS_UPDATE_DELAY);
    }
}
