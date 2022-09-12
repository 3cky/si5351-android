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

package com.github.ykc3.android.si5351;

import com.github.ykc3.android.usbi2c.UsbI2cAdapter;
import com.github.ykc3.android.usbi2c.UsbI2cDevice;

import java.io.IOException;

import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK0;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK1;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK2;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK3;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK4;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK5;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK6;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK7;
import static com.github.ykc3.android.si5351.Si5351.si5351_pll.SI5351_PLLA;
import static com.github.ykc3.android.si5351.Si5351.si5351_pll.SI5351_PLLB;
import static com.github.ykc3.android.si5351.Si5351.si5351_pll_input.SI5351_PLL_INPUT_CLKIN;
import static com.github.ykc3.android.si5351.Si5351.si5351_pll_input.SI5351_PLL_INPUT_XO;

public class Si5351 {
    public static final int SI5351_BUS_BASE_ADDR = 0x60;

    public static final int SI5351_XTAL_FREQ = 25_000_000;

    public static final long SI5351_PLL_FIXED = 800_000_000_00L;
    public static final long SI5351_FREQ_MULT = 100L;

    public static final int SI5351_PLL_A_MIN = 15;
    public static final int SI5351_PLL_A_MAX = 90;
    public static final int SI5351_PLL_C_MAX = 1048575;
    public static final int SI5351_PLL_B_MAX = (SI5351_PLL_C_MAX - 1);
    public static final int SI5351_MULTISYNTH_A_MIN = 6;
    public static final int SI5351_MULTISYNTH_A_MAX = 1800;
    public static final int SI5351_MULTISYNTH67_A_MAX = 254;
    public static final int SI5351_MULTISYNTH_C_MAX = 1048575;
    public static final int SI5351_MULTISYNTH_B_MAX = (SI5351_MULTISYNTH_C_MAX - 1);
    public static final int SI5351_MULTISYNTH_P1_MAX = ((1 << 18) - 1);
    public static final int SI5351_MULTISYNTH_P2_MAX = ((1 << 20) - 1);
    public static final int SI5351_MULTISYNTH_P3_MAX = ((1 << 20) - 1);
    public static final int SI5351_VCXO_PL_MIN = 30;
    public static final int SI5351_VCXO_PL_MAX = 240;
    public static final int SI5351_VCXO_MARGIN = 103;

    public static final int SI5351_PLL_VCO_MIN = 600_000_000;
    public static final int SI5351_PLL_VCO_MAX = 900_000_000;
    public static final int SI5351_MULTISYNTH_MIN_FREQ = 500000;
    public static final int SI5351_MULTISYNTH_DIVBY4_FREQ = 150_000_000;
    public static final int SI5351_MULTISYNTH_MAX_FREQ = 225_000_000;
    public static final int SI5351_MULTISYNTH_SHARE_MAX = 100000000;
    public static final int SI5351_MULTISYNTH_SHARE_MIN = 1024000;
    public static final int SI5351_MULTISYNTH67_MAX_FREQ = SI5351_MULTISYNTH_DIVBY4_FREQ;
    public static final int SI5351_CLKOUT_MIN_FREQ = 4000;
    public static final int SI5351_CLKOUT_MAX_FREQ = SI5351_MULTISYNTH_MAX_FREQ;
    public static final int SI5351_CLKOUT67_MS_MIN = SI5351_PLL_VCO_MIN / SI5351_MULTISYNTH67_A_MAX;
    public static final int SI5351_CLKOUT67_MIN_FREQ = SI5351_CLKOUT67_MS_MIN / 128;
    public static final int SI5351_CLKOUT67_MAX_FREQ = SI5351_MULTISYNTH67_MAX_FREQ;


    public static final int SI5351_DEVICE_STATUS = 0;
    public static final int SI5351_INTERRUPT_STATUS = 1;
    public static final int SI5351_INTERRUPT_MASK = 2;
    public static final int SI5351_STATUS_SYS_INIT = (1 << 7);
    public static final int SI5351_STATUS_LOL_B = (1 << 6);
    public static final int SI5351_STATUS_LOL_A = (1 << 5);
    public static final int SI5351_STATUS_LOS = (1 << 4);
    public static final int SI5351_OUTPUT_ENABLE_CTRL = 3;
    public static final int SI5351_OEB_PIN_ENABLE_CTRL = 9;
    public static final int SI5351_PLL_INPUT_SOURCE = 15;
    public static final int SI5351_CLKIN_DIV_MASK = (3 << 6);
    public static final int SI5351_CLKIN_DIV_1 = (0 << 6);
    public static final int SI5351_CLKIN_DIV_2 = (1 << 6);
    public static final int SI5351_CLKIN_DIV_4 = (2 << 6);
    public static final int SI5351_CLKIN_DIV_8 = (3 << 6);
    public static final int SI5351_PLLB_SOURCE = (1 << 3);
    public static final int SI5351_PLLA_SOURCE = (1 << 2);

    public static final int SI5351_CLK0_CTRL = 16;
    public static final int SI5351_CLK1_CTRL = 17;
    public static final int SI5351_CLK2_CTRL = 18;
    public static final int SI5351_CLK3_CTRL = 19;
    public static final int SI5351_CLK4_CTRL = 20;
    public static final int SI5351_CLK5_CTRL = 21;
    public static final int SI5351_CLK6_CTRL = 22;
    public static final int SI5351_CLK7_CTRL = 23;
    public static final int SI5351_CLK_POWERDOWN = (1 << 7);
    public static final int SI5351_CLK_INTEGER_MODE = (1 << 6);
    public static final int SI5351_CLK_PLL_SELECT = (1 << 5);
    public static final int SI5351_CLK_INVERT = (1 << 4);
    public static final int SI5351_CLK_INPUT_MASK = (3 << 2);
    public static final int SI5351_CLK_INPUT_XTAL = (0 << 2);
    public static final int SI5351_CLK_INPUT_CLKIN = (1 << 2);
    public static final int SI5351_CLK_INPUT_MULTISYNTH_0_4 = (2 << 2);
    public static final int SI5351_CLK_INPUT_MULTISYNTH_N = (3 << 2);
    public static final int SI5351_CLK_DRIVE_STRENGTH_MASK = 3;
    public static final int SI5351_CLK_DRIVE_STRENGTH_2MA = 0;
    public static final int SI5351_CLK_DRIVE_STRENGTH_4MA = 1;
    public static final int SI5351_CLK_DRIVE_STRENGTH_6MA = 2;
    public static final int SI5351_CLK_DRIVE_STRENGTH_8MA = 3;

    public static final int SI5351_CLK3_0_DISABLE_STATE = 24;
    public static final int SI5351_CLK7_4_DISABLE_STATE = 25;
    public static final int SI5351_CLK_DISABLE_STATE_MASK = 3;
    public static final int SI5351_CLK_DISABLE_STATE_LOW = 0;
    public static final int SI5351_CLK_DISABLE_STATE_HIGH = 1;
    public static final int SI5351_CLK_DISABLE_STATE_FLOAT = 2;
    public static final int SI5351_CLK_DISABLE_STATE_NEVER = 3;

    public static final int SI5351_PARAMETERS_LENGTH = 8;
    public static final int SI5351_PLLA_PARAMETERS = 26;
    public static final int SI5351_PLLB_PARAMETERS = 34;
    public static final int SI5351_CLK0_PARAMETERS = 42;
    public static final int SI5351_CLK1_PARAMETERS = 50;
    public static final int SI5351_CLK2_PARAMETERS = 58;
    public static final int SI5351_CLK3_PARAMETERS = 66;
    public static final int SI5351_CLK4_PARAMETERS = 74;
    public static final int SI5351_CLK5_PARAMETERS = 82;
    public static final int SI5351_CLK6_PARAMETERS = 90;
    public static final int SI5351_CLK7_PARAMETERS = 91;
    public static final int SI5351_CLK6_7_OUTPUT_DIVIDER = 92;
    public static final int SI5351_OUTPUT_CLK_DIV_MASK = (7 << 4);
    public static final int SI5351_OUTPUT_CLK6_DIV_MASK = 7;
    public static final int SI5351_OUTPUT_CLK_DIV_SHIFT = 4;
    public static final int SI5351_OUTPUT_CLK_DIV6_SHIFT = 0;
    public static final int SI5351_OUTPUT_CLK_DIV_1 = 0;
    public static final int SI5351_OUTPUT_CLK_DIV_2 = 1;
    public static final int SI5351_OUTPUT_CLK_DIV_4 = 2;
    public static final int SI5351_OUTPUT_CLK_DIV_8 = 3;
    public static final int SI5351_OUTPUT_CLK_DIV_16 = 4;
    public static final int SI5351_OUTPUT_CLK_DIV_32 = 5;
    public static final int SI5351_OUTPUT_CLK_DIV_64 = 6;
    public static final int SI5351_OUTPUT_CLK_DIV_128 = 7;
    public static final int SI5351_OUTPUT_CLK_DIVBY4 = (3 << 2);

    public static final int SI5351_SSC_PARAM0 = 149;
    public static final int SI5351_SSC_PARAM1 = 150;
    public static final int SI5351_SSC_PARAM2 = 151;
    public static final int SI5351_SSC_PARAM3 = 152;
    public static final int SI5351_SSC_PARAM4 = 153;
    public static final int SI5351_SSC_PARAM5 = 154;
    public static final int SI5351_SSC_PARAM6 = 155;
    public static final int SI5351_SSC_PARAM7 = 156;
    public static final int SI5351_SSC_PARAM8 = 157;
    public static final int SI5351_SSC_PARAM9 = 158;
    public static final int SI5351_SSC_PARAM10 = 159;
    public static final int SI5351_SSC_PARAM11 = 160;
    public static final int SI5351_SSC_PARAM12 = 161;

    public static final int SI5351_VXCO_PARAMETERS_LOW = 162;
    public static final int SI5351_VXCO_PARAMETERS_MID = 163;
    public static final int SI5351_VXCO_PARAMETERS_HIGH = 164;

    public static final int SI5351_CLK0_PHASE_OFFSET = 165;
    public static final int SI5351_CLK1_PHASE_OFFSET = 166;
    public static final int SI5351_CLK2_PHASE_OFFSET = 167;
    public static final int SI5351_CLK3_PHASE_OFFSET = 168;
    public static final int SI5351_CLK4_PHASE_OFFSET = 169;
    public static final int SI5351_CLK5_PHASE_OFFSET = 170;

    public static final int SI5351_PLL_RESET = 177;
    public static final int SI5351_PLL_RESET_B = (1 << 7);
    public static final int SI5351_PLL_RESET_A = (1 << 5);

    public static final int SI5351_CRYSTAL_LOAD = 183;
    public static final int SI5351_CRYSTAL_LOAD_MASK = (3 << 6);
    public static final int SI5351_CRYSTAL_LOAD_0PF = (0 << 6);
    public static final int SI5351_CRYSTAL_LOAD_6PF = (1 << 6);
    public static final int SI5351_CRYSTAL_LOAD_8PF = (2 << 6);
    public static final int SI5351_CRYSTAL_LOAD_10PF = (3 << 6);

    public static final int SI5351_FANOUT_ENABLE = 187;
    public static final int SI5351_CLKIN_ENABLE = (1 << 7);
    public static final int SI5351_XTAL_ENABLE = (1 << 6);
    public static final int SI5351_MULTISYNTH_ENABLE = (1 << 4);

    public static final int RFRAC_DENOM = 1000000;

    public enum si5351_clock {
        SI5351_CLK0, SI5351_CLK1, SI5351_CLK2, SI5351_CLK3,
        SI5351_CLK4, SI5351_CLK5, SI5351_CLK6, SI5351_CLK7
    }

    public enum si5351_pll {
        SI5351_PLLA, SI5351_PLLB
    }

    public enum si5351_drive {
        SI5351_DRIVE_2MA, SI5351_DRIVE_4MA, SI5351_DRIVE_6MA, SI5351_DRIVE_8MA
    }

    public enum si5351_clock_source {
        SI5351_CLK_SRC_XTAL, SI5351_CLK_SRC_CLKIN, SI5351_CLK_SRC_MS0, SI5351_CLK_SRC_MS
    }

    public enum si5351_clock_disable {
        SI5351_CLK_DISABLE_LOW, SI5351_CLK_DISABLE_HIGH, SI5351_CLK_DISABLE_HI_Z, SI5351_CLK_DISABLE_NEVER
    }

    public enum si5351_clock_fanout {
        SI5351_FANOUT_CLKIN, SI5351_FANOUT_XO, SI5351_FANOUT_MS
    }

    public enum si5351_pll_input {
        SI5351_PLL_INPUT_XO, SI5351_PLL_INPUT_CLKIN
    }

    /* Struct definitions */

    public static class Si5351RegSet {
        public int p1;
        public int p2;
        public int p3;
    }

    public static class Si5351Status {
        public boolean SYS_INIT;
        public boolean LOL_B;
        public boolean LOL_A;
        public boolean LOS;
        public int REVID;
    }

    public static class Si5351IntStatus {
        public boolean SYS_INIT_STKY;
        public boolean LOL_B_STKY;
        public boolean LOL_A_STKY;
        public boolean LOS_STKY;
    }

    // Public

    public final Si5351Status dev_status = new Si5351Status();
    public final Si5351IntStatus dev_int_status = new Si5351IntStatus();

    public final si5351_pll[] pll_assignment = new si5351_pll[si5351_clock.values().length];

    public final long[] clk_freq = new long[si5351_clock.values().length];

    public long plla_freq;
    public long pllb_freq;

    public si5351_pll_input plla_ref_osc;
    public si5351_pll_input pllb_ref_osc;

    public final int[] xtal_freq = new int[si5351_pll_input.values().length];

    // Private

    private final int[] ref_correction = new int[si5351_pll_input.values().length];
    private int clkin_div;
    private final boolean[] clk_first_set = new boolean[si5351_clock.values().length];

    private final UsbI2cDevice device;

    // Public functions

    public Si5351(UsbI2cAdapter i2cAdapter) {
        this(i2cAdapter, SI5351_BUS_BASE_ADDR);
    }

    public Si5351(UsbI2cAdapter i2cAdapter, int i2c_addr) {
        device = i2cAdapter.getDevice(i2c_addr);

        xtal_freq[0] = SI5351_XTAL_FREQ;

        // Start by using XO ref osc as default for each PLL
        plla_ref_osc = SI5351_PLL_INPUT_XO;
        pllb_ref_osc = SI5351_PLL_INPUT_XO;

        clkin_div = SI5351_CLKIN_DIV_1;
    }

    /**
     * Setup communications to the Si5351 and set the crystal
     * load capacitance.
     *
     * xtal_load_c - Crystal load capacitance. Use the SI5351_CRYSTAL_LOAD_*PF
     * defines in the header file
     * xo_freq - Crystal/reference oscillator frequency in 1 Hz increments.
     * Defaults to SI5351_XTAL_FREQ if a 0 is used here.
     * corr - Frequency correction constant in parts-per-billion
     */
    public void init(int xtal_load_c, int xo_freq, int corr) throws IOException {
        // Wait for SYS_INIT flag to be clear, indicating that device is ready
        byte status_reg;
        do {
            status_reg = si5351_read(SI5351_DEVICE_STATUS);
        } while (((status_reg >> 7) & 0x01) == 1);

        // Set crystal load capacitance
        si5351_write(SI5351_CRYSTAL_LOAD, (byte) ((xtal_load_c & SI5351_CRYSTAL_LOAD_MASK) | 0b00010010));

        // Set up the XO reference frequency
        if (xo_freq != 0) {
            set_ref_freq(xo_freq, SI5351_PLL_INPUT_XO);
        } else {
            set_ref_freq(SI5351_XTAL_FREQ, SI5351_PLL_INPUT_XO);
        }

        // Set the frequency calibration for the XO
        set_correction(corr, SI5351_PLL_INPUT_XO);

        reset();
    }

    /**
     * Call to reset the Si5351 to the state initialized by the library.
     */
    public void reset() throws IOException {
        // Initialize the CLK outputs according to flowchart in datasheet
        // First, turn them off
        si5351_write(16, (byte) 0x80);
        si5351_write(17, (byte) 0x80);
        si5351_write(18, (byte) 0x80);
        si5351_write(19, (byte) 0x80);
        si5351_write(20, (byte) 0x80);
        si5351_write(21, (byte) 0x80);
        si5351_write(22, (byte) 0x80);
        si5351_write(23, (byte) 0x80);

        // Turn the clocks back on...
        si5351_write(16, (byte) 0x0c);
        si5351_write(17, (byte) 0x0c);
        si5351_write(18, (byte) 0x0c);
        si5351_write(19, (byte) 0x0c);
        si5351_write(20, (byte) 0x0c);
        si5351_write(21, (byte) 0x0c);
        si5351_write(22, (byte) 0x0c);
        si5351_write(23, (byte) 0x0c);

        // Set PLLA and PLLB to 800 MHz for automatic tuning
        set_pll(SI5351_PLL_FIXED, SI5351_PLLA);
        set_pll(SI5351_PLL_FIXED, SI5351_PLLB);

        // Make PLL to CLK assignments for automatic tuning
        pll_assignment[0] = SI5351_PLLA;
        pll_assignment[1] = SI5351_PLLA;
        pll_assignment[2] = SI5351_PLLA;
        pll_assignment[3] = SI5351_PLLA;
        pll_assignment[4] = SI5351_PLLA;
        pll_assignment[5] = SI5351_PLLA;
        pll_assignment[6] = SI5351_PLLB;
        pll_assignment[7] = SI5351_PLLB;

        set_ms_source(SI5351_CLK0, SI5351_PLLA);
        set_ms_source(SI5351_CLK1, SI5351_PLLA);
        set_ms_source(SI5351_CLK2, SI5351_PLLA);
        set_ms_source(SI5351_CLK3, SI5351_PLLA);
        set_ms_source(SI5351_CLK4, SI5351_PLLA);
        set_ms_source(SI5351_CLK5, SI5351_PLLA);
        set_ms_source(SI5351_CLK6, SI5351_PLLB);
        set_ms_source(SI5351_CLK7, SI5351_PLLB);

        // Reset the VCXO param
        si5351_write(SI5351_VXCO_PARAMETERS_LOW, (byte) 0);
        si5351_write(SI5351_VXCO_PARAMETERS_MID, (byte) 0);
        si5351_write(SI5351_VXCO_PARAMETERS_HIGH, (byte) 0);

        // Then reset the PLLs
        pll_reset(SI5351_PLLA);
        pll_reset(SI5351_PLLB);

        // Set initial frequencies
        for (si5351_clock clk : si5351_clock.values()) {
            clk_freq[clk.ordinal()] = 0;
            output_enable(clk, false);
            clk_first_set[clk.ordinal()] = false;
        }
    }

    /**
     * Sets the clock frequency of the specified CLK output.
     * Frequency range of 8 kHz to 150 MHz.
     *
     * freq - Output frequency in Hz
     * clk - Clock output (use the si5351_clock enum)
     */
    public boolean set_freq(long freq, si5351_clock clk) throws IOException {
        Si5351RegSet ms_reg = new Si5351RegSet();
        long pll_freq;
        boolean int_mode = false;
        boolean div_by_4 = false;
        int r_div;

        // Check which Multisynth is being set
        if (clk.ordinal() <= SI5351_CLK5.ordinal()) {
            // MS0 through MS5 logic
            // ---------------------

            // Lower bounds check
            if (freq > 0 && freq < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT) {
                freq = SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT;
            }

            // Upper bounds check
            if (freq > SI5351_MULTISYNTH_MAX_FREQ * SI5351_FREQ_MULT) {
                freq = SI5351_MULTISYNTH_MAX_FREQ * SI5351_FREQ_MULT;
            }

            // If requested freq >100 MHz and no other outputs are already >100 MHz,
            // we need to recalculate PLLA and then recalculate all other CLK outputs
            // on same PLL
            if (freq > (SI5351_MULTISYNTH_SHARE_MAX * SI5351_FREQ_MULT)) {
                // Check other clocks on same PLL
                for (int i = 0; i < 6; i++) {
                    if (clk_freq[i] > (SI5351_MULTISYNTH_SHARE_MAX * SI5351_FREQ_MULT)) {
                        if (i != clk.ordinal() && pll_assignment[i] == pll_assignment[clk.ordinal()]) {
                            return true; // won't set if any other clks already >100 MHz
                        }
                    }
                }

                // Enable the output on first set_freq only
                if (!clk_first_set[clk.ordinal()]) {
                    output_enable(clk, true);
                    clk_first_set[clk.ordinal()] = true;
                }

                // Set the freq in memory
                clk_freq[clk.ordinal()] = freq;

                // Calculate the proper PLL frequency
                pll_freq = multisynth_calc(freq, 0, ms_reg);

                // Set PLL
                set_pll(pll_freq, pll_assignment[clk.ordinal()]);

                // Recalculate params for other synths on same PLL
                for (int i = 0; i < 6; i++) {
                    if (clk_freq[i] != 0) {
                        if (pll_assignment[i] == pll_assignment[clk.ordinal()]) {
                            Si5351RegSet temp_reg = new Si5351RegSet();

                            long[] temp_freq = new long[1];

                            // Select the proper R div value
                            temp_freq[0] = clk_freq[i];
                            r_div = select_r_div(temp_freq);

                            multisynth_calc(temp_freq[0], pll_freq, temp_reg);

                            // If freq > 150 MHz, we need to use DIVBY4 and integer mode
                            if (temp_freq[0] >= SI5351_MULTISYNTH_DIVBY4_FREQ * SI5351_FREQ_MULT) {
                                div_by_4 = true;
                                int_mode = true;
                            } else {
                                div_by_4 = false;
                                int_mode = false;
                            }

                            // Set multisynth registers
                            set_ms(si5351_clock.values()[i], temp_reg, int_mode, r_div, div_by_4);
                        }
                    }
                }

                // Reset the PLL
                pll_reset(pll_assignment[clk.ordinal()]);
            } else {
                clk_freq[clk.ordinal()] = freq;

                // Enable the output on first set_freq only
                if (!clk_first_set[clk.ordinal()]) {
                    output_enable(clk, true);
                    clk_first_set[clk.ordinal()] = true;
                }

                // Select the proper R div value
                long[] temp_freq = new long[]{freq};
                r_div = select_r_div(temp_freq);
                freq = temp_freq[0];

                // Calculate the synth parameters
                if (pll_assignment[clk.ordinal()] == SI5351_PLLA) {
                    multisynth_calc(freq, plla_freq, ms_reg);
                } else {
                    multisynth_calc(freq, pllb_freq, ms_reg);
                }

                // Set multisynth registers
                set_ms(clk, ms_reg, int_mode, r_div, div_by_4);

                // Reset the PLL
                //pll_reset(pll_assignment[clk]);
            }

            return false;
        } else {
            // MS6 and MS7 logic
            // -----------------

            // Lower bounds check
            if (freq > 0 && freq < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT) {
                freq = SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT;
            }

            // Upper bounds check
            if (freq >= SI5351_MULTISYNTH_DIVBY4_FREQ * SI5351_FREQ_MULT) {
                freq = SI5351_MULTISYNTH_DIVBY4_FREQ * SI5351_FREQ_MULT - 1;
            }

            // If one of CLK6 or CLK7 is already set when trying to set the other,
            // we have to ensure that it will also have an integer division ratio
            // with the same PLL, otherwise do not set it.
            if (clk == SI5351_CLK6) {
                if (clk_freq[7] != 0) {
                    if (pllb_freq % freq == 0) {
                        if ((pllb_freq / freq) % 2 != 0) {
                            // Not an even divide ratio, no bueno
                            return true;
                        } else {
                            // Set the freq in memory
                            clk_freq[clk.ordinal()] = freq;

                            // Select the proper R div value
                            long[] temp_freq = new long[]{freq};
                            r_div = select_r_div_ms67(temp_freq);
                            freq = temp_freq[0];

                            multisynth67_calc(freq, pllb_freq, ms_reg);
                        }
                    } else {
                        // Not an integer divide ratio, no good
                        return true;
                    }
                } else {
                    // No previous assignment, so set PLLB based on CLK6

                    // Set the freq in memory
                    clk_freq[clk.ordinal()] = freq;

                    // Select the proper R div value
                    long[] temp_freq = new long[]{freq};
                    r_div = select_r_div_ms67(temp_freq);
                    freq = temp_freq[0];

                    pll_freq = multisynth67_calc(freq, 0, ms_reg);
                    //pllb_freq = pll_freq;
                    set_pll(pll_freq, SI5351_PLLB);
                }
            } else {
                if (clk_freq[6] != 0) {
                    if (pllb_freq % freq == 0) {
                        if ((pllb_freq / freq) % 2 != 0) {
                            // Not an even divide ratio, no bueno
                            return true;
                        } else {
                            // Set the freq in memory
                            clk_freq[clk.ordinal()] = freq;

                            // Select the proper R div value
                            long[] temp_freq = new long[]{freq};
                            r_div = select_r_div_ms67(temp_freq);
                            freq = temp_freq[0];

                            multisynth67_calc(freq, pllb_freq, ms_reg);
                        }
                    } else {
                        // Not an integer divide ratio, no good
                        return true;
                    }
                } else {
                    // No previous assignment, so set PLLB based on CLK7

                    // Set the freq in memory
                    clk_freq[clk.ordinal()] = freq;

                    // Select the proper R div value
                    long[] temp_freq = new long[]{freq};
                    r_div = select_r_div_ms67(temp_freq);
                    freq = temp_freq[0];

                    pll_freq = multisynth67_calc(freq, 0, ms_reg);
                    //pllb_freq = pll_freq;
                    set_pll(pll_freq, pll_assignment[clk.ordinal()]);
                }
            }

            div_by_4 = false;
            int_mode = false;

            // Set multisynth registers (MS must be set before PLL)
            set_ms(clk, ms_reg, int_mode, r_div, div_by_4);

            return false;
        }
    }

    /**
     * Sets the clock frequency of the specified CLK output using the given PLL
     * frequency. You must ensure that the MS is assigned to the correct PLL and
     * that the PLL is set to the correct frequency before using this method.
     *
     * It is important to note that if you use this method, you will have to
     * track that all settings are sane yourself.
     *
     * freq - Output frequency in Hz
     * pll_freq - Frequency of the PLL driving the Multisynth in Hz * 100
     * clk - Clock output
     *   (use the si5351_clock enum)
     */
    public void set_freq_manual(long freq, long pll_freq, si5351_clock clk) throws IOException {
        Si5351RegSet ms_reg = new Si5351RegSet();
        boolean int_mode = false;
        boolean div_by_4 = false;

        // Lower bounds check
        if (freq > 0 && freq < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT;
        }

        // Upper bounds check
        if (freq > SI5351_CLKOUT_MAX_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_CLKOUT_MAX_FREQ * SI5351_FREQ_MULT;
        }

        int r_div;

        clk_freq[clk.ordinal()] = freq;

        set_pll(pll_freq, pll_assignment[clk.ordinal()]);

        // Enable the output
        output_enable(clk, true);

        // Select the proper R div value
        long[] temp_freq = new long[] { freq };
        r_div = select_r_div(temp_freq);
        freq = temp_freq[0];

        // Calculate the synth parameters
        multisynth_calc(freq, pll_freq, ms_reg);

        // If freq > 150 MHz, we need to use DIVBY4 and integer mode
        if (freq >= SI5351_MULTISYNTH_DIVBY4_FREQ * SI5351_FREQ_MULT) {
            div_by_4 = true;
            int_mode = true;
        }

        // Set multisynth registers (MS must be set before PLL)
        set_ms(clk, ms_reg, int_mode, r_div, div_by_4);
    }

    /**
     * Set the specified PLL to a specific oscillation frequency.
     *
     * pll_freq - Desired PLL frequency in Hz * 100
     * target_pll - Which PLL to set
     *     (use the si5351_pll enum)
     */
    public void set_pll(long pll_freq, si5351_pll target_pll) throws IOException {
        Si5351RegSet pll_reg = new Si5351RegSet();

        if (target_pll == SI5351_PLLA) {
            pll_calc(SI5351_PLLA, pll_freq, pll_reg, ref_correction[plla_ref_osc.ordinal()], false);
        } else {
            pll_calc(SI5351_PLLB, pll_freq, pll_reg, ref_correction[pllb_ref_osc.ordinal()], false);
        }

        // Derive the register values to write

        // Prepare an array for parameters to be written to
        byte[] params = new byte[20];
        int i = 0;
        byte temp;

        // Registers 26-27
        temp = (byte) ((pll_reg.p3 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p3 & 0xFF);
        params[i++] = temp;

        // Register 28
        temp = (byte) ((pll_reg.p1 >> 16) & 0x03);
        params[i++] = temp;

        // Registers 29-30
        temp = (byte) ((pll_reg.p1 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p1 & 0xFF);
        params[i++] = temp;

        // Register 31
        temp = (byte) ((pll_reg.p3 >> 12) & 0xF0);
        temp += (byte) ((pll_reg.p2 >> 16) & 0x0F);
        params[i++] = temp;

        // Registers 32-33
        temp = (byte) ((pll_reg.p2 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p2 & 0xFF);
        params[i++] = temp;

        // Write the parameters
        if (target_pll == SI5351_PLLA) {
            si5351_write_bulk(SI5351_PLLA_PARAMETERS, i, params);
            plla_freq = pll_freq;
        } else if (target_pll == SI5351_PLLB) {
            si5351_write_bulk(SI5351_PLLB_PARAMETERS, i, params);
            pllb_freq = pll_freq;
        }
    }

    /**
     * Set the specified multisynth parameters. Not normally needed, but public for advanced users.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * int_mode - Set integer mode. Set to true to enable, false to disable.
     * r_div - Desired r_div ratio
     * div_by_4 - Set Divide By 4 mode. Set to true to enable, false to disable.
     */
    public void set_ms(si5351_clock clk, Si5351RegSet ms_reg, boolean int_mode, int r_div, boolean div_by_4) throws IOException {
        byte[] params = new byte[20];
        int i = 0;
        byte temp;
        byte reg_val;

        if (clk.ordinal() <= SI5351_CLK5.ordinal()) {
            // Registers 42-43 for CLK0
            temp = (byte) ((ms_reg.p3 >> 8) & 0xFF);
            params[i++] = temp;

            temp = (byte) (ms_reg.p3 & 0xFF);
            params[i++] = temp;

            // Register 44 for CLK0
            reg_val = si5351_read((SI5351_CLK0_PARAMETERS + 2) + (clk.ordinal() * 8));
            reg_val &= ~(0x03);
            temp = (byte) (reg_val | ((byte) ((ms_reg.p1 >> 16) & 0x03)));
            params[i++] = temp;

            // Registers 45-46 for CLK0
            temp = (byte) ((ms_reg.p1 >> 8) & 0xFF);
            params[i++] = temp;

            temp = (byte) (ms_reg.p1 & 0xFF);
            params[i++] = temp;

            // Register 47 for CLK0
            temp = (byte) ((ms_reg.p3 >> 12) & 0xF0);
            temp += (byte) ((ms_reg.p2 >> 16) & 0x0F);
            params[i++] = temp;

            // Registers 48-49 for CLK0
            temp = (byte) ((ms_reg.p2 >> 8) & 0xFF);
            params[i++] = temp;

            temp = (byte) (ms_reg.p2 & 0xFF);
            params[i++] = temp;
        } else {
            // MS6 and MS7 only use one register
            temp = (byte) ms_reg.p1;
        }

        // Write the parameters
        switch (clk) {
            case SI5351_CLK0:
                si5351_write_bulk(SI5351_CLK0_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK1:
                si5351_write_bulk(SI5351_CLK1_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK2:
                si5351_write_bulk(SI5351_CLK2_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK3:
                si5351_write_bulk(SI5351_CLK3_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK4:
                si5351_write_bulk(SI5351_CLK4_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK5:
                si5351_write_bulk(SI5351_CLK5_PARAMETERS, i, params);
                set_int(clk, int_mode);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK6:
                si5351_write(SI5351_CLK6_PARAMETERS, temp);
                ms_div(clk, r_div, div_by_4);
                break;
            case SI5351_CLK7:
                si5351_write(SI5351_CLK7_PARAMETERS, temp);
                ms_div(clk, r_div, div_by_4);
                break;
        }
    }

    /**
     * Enable or disable a chosen output
     * clk - Clock output
     *   (use the si5351_clock enum)
     * enable - Set to true to enable, false to disable
     */
    public void output_enable(si5351_clock clk, boolean enable) throws IOException {
        byte reg_val;

        reg_val = si5351_read(SI5351_OUTPUT_ENABLE_CTRL);

        if (enable) {
            reg_val &= ~(1 << clk.ordinal());
        } else {
            reg_val |= (1 << clk.ordinal());
        }

        si5351_write(SI5351_OUTPUT_ENABLE_CTRL, reg_val);
    }

    /**
     * Sets the drive strength of the specified clock output.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * drive - Desired drive level
     *   (use the si5351_drive enum)
     */
    public void drive_strength(si5351_clock clk, si5351_drive drive) throws IOException {
        byte mask = 0x03;

        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());
        reg_val &= ~(mask);

        switch (drive) {
            case SI5351_DRIVE_2MA:
                reg_val |= 0x00;
                break;
            case SI5351_DRIVE_4MA:
                reg_val |= 0x01;
                break;
            case SI5351_DRIVE_6MA:
                reg_val |= 0x02;
                break;
            case SI5351_DRIVE_8MA:
                reg_val |= 0x03;
                break;
            default:
                break;
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);
    }

    /**
     * Call this to update the status structs, then access them
     * via the dev_status and dev_int_status global members.
     *
     * See the header file for the struct definitions. These
     * correspond to the flag names for registers 0 and 1 in
     * the Si5351 datasheet.
     */
    public void update_status() throws IOException {
        update_sys_status(dev_status);
        update_int_status(dev_int_status);
    }

    /**
     * Use this to set the oscillator correction factor.
     * This value is a signed 32-bit integer of the
     * parts-per-billion value that the actual oscillation
     * frequency deviates from the specified frequency.
     *
     * The frequency calibration is done as a one-time procedure.
     * Any desired test frequency within the normal range of the
     * Si5351 should be set, then the actual output frequency
     * should be measured as accurately as possible. The
     * difference between the measured and specified frequencies
     * should be calculated in Hertz, then multiplied by 10 in
     * order to get the parts-per-billion value.
     *
     * Since the Si5351 itself has an intrinsic 0 PPM error, this
     * correction factor is good across the entire tuning range of
     * the Si5351. Once this calibration is done accurately, it
     * should not have to be done again for the same Si5351 and
     * crystal.
     *
     * corr - Correction factor in ppb
     * ref_osc - Desired reference oscillator
     *     (use the si5351_pll_input enum)
     */
    public void set_correction(int corr, si5351_pll_input ref_osc) throws IOException {
        ref_correction[ref_osc.ordinal()] = corr;

        // Recalculate and set PLL freqs based on correction value
        set_pll(plla_freq, SI5351_PLLA);
        set_pll(pllb_freq, SI5351_PLLB);
    }

    /**
     * Write the 7-bit phase register. This must be used
     * with a user-set PLL frequency so that the user can
     * calculate the proper tuning word based on the PLL period.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * phase - 7-bit phase word
     *   (in units of VCO/4 period)
     */
    public void set_phase(si5351_clock clk, int phase) throws IOException {
        // Mask off the upper bit since it is reserved
        phase = phase & 0b01111111;

        si5351_write(SI5351_CLK0_PHASE_OFFSET + clk.ordinal(), (byte) phase);
    }

    /**
     * Returns the oscillator correction factor stored
     * in RAM.
     *
     * ref_osc - Desired reference oscillator
     *     0: crystal oscillator (XO)
     *     1: external clock input (CLKIN)
     */
    public int get_correction(si5351_pll_input ref_osc) {
        return ref_correction[ref_osc.ordinal()];
    }

    /**
     * Apply a reset to the indicated PLL.
     *
     * target_pll - Which PLL to reset
     *     (use the si5351_pll enum)
     */
    public void pll_reset(si5351_pll target_pll) throws IOException {
        if (target_pll == SI5351_PLLA) {
            si5351_write(SI5351_PLL_RESET, (byte) SI5351_PLL_RESET_A);
        } else if (target_pll == SI5351_PLLB) {
            si5351_write(SI5351_PLL_RESET, (byte) SI5351_PLL_RESET_B);
        }
    }

    /**
     * Set the desired PLL source for a multisynth.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * pll - Which PLL to use as the source
     *     (use the si5351_pll enum)
     */
    public void set_ms_source(si5351_clock clk, si5351_pll pll) throws IOException {
        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());

        if (pll == si5351_pll.SI5351_PLLA) {
            reg_val &= ~(SI5351_CLK_PLL_SELECT);
        } else if (pll == si5351_pll.SI5351_PLLB) {
            reg_val |= SI5351_CLK_PLL_SELECT;
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);

        pll_assignment[clk.ordinal()] = pll;
    }

    /**
     * Set the indicated multisynth into integer mode.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * enable - Set to true to enable, false to disable
     */
    public void set_int(si5351_clock clk, boolean enable) throws IOException {
        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());

        if (enable) {
            reg_val |= (SI5351_CLK_INTEGER_MODE);
        } else {
            reg_val &= ~(SI5351_CLK_INTEGER_MODE);
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);
    }

    /**
     * Enable or disable power to a clock output (a power
     * saving feature).
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * pwr - Set to true to enable, false to disable
     */
    public void set_clock_pwr(si5351_clock clk, boolean pwr) throws IOException {
        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());

        if (pwr) {
            reg_val &= 0b01111111;
        } else {
            reg_val |= 0b10000000;
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);
    }

    /**
     * Enable to invert the clock output waveform.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * inv - Set to true to enable, false to disable
     */
    public void set_clock_invert(si5351_clock clk, boolean inv) throws IOException {
        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());

        if (inv) {
            reg_val |= (SI5351_CLK_INVERT);
        } else {
            reg_val &= ~(SI5351_CLK_INVERT);
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);
    }

    /**
     * Set the clock source for a multisynth (based on the options
     * presented for Registers 16-23 in the Silicon Labs AN619 document).
     * Choices are XTAL, CLKIN, MS0, or the multisynth associated with
     * the clock output.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * src - Which clock source to use for the multisynth
     *   (use the si5351_clock_source enum)
     */
    public void set_clock_source(si5351_clock clk, si5351_clock_source src) throws IOException {
        byte reg_val = si5351_read(SI5351_CLK0_CTRL + clk.ordinal());

        // Clear the bits first
        reg_val &= ~(SI5351_CLK_INPUT_MASK);

        switch (src) {
            case SI5351_CLK_SRC_XTAL:
                reg_val |= (SI5351_CLK_INPUT_XTAL);
                break;
            case SI5351_CLK_SRC_CLKIN:
                reg_val |= (SI5351_CLK_INPUT_CLKIN);
                break;
            case SI5351_CLK_SRC_MS0:
                if (clk == SI5351_CLK0) {
                    return;
                }
                reg_val |= (SI5351_CLK_INPUT_MULTISYNTH_0_4);
                break;
            case SI5351_CLK_SRC_MS:
                reg_val |= (SI5351_CLK_INPUT_MULTISYNTH_N);
                break;
            default:
                return;
        }

        si5351_write(SI5351_CLK0_CTRL + clk.ordinal(), reg_val);
    }

    /**
     * Set the state of the clock output when it is disabled. Per page 27
     * of AN619 (Registers 24 and 25), there are four possible values: low,
     * high, high impedance, and never disabled.
     *
     * clk - Clock output
     *   (use the si5351_clock enum)
     * dis_state - Desired state of the output upon disable
     *   (use the si5351_clock_disable enum)
     */
    public void set_clock_disable(si5351_clock clk, si5351_clock_disable dis_state) throws IOException {
        byte reg_val, reg;

        if (clk.ordinal() >= SI5351_CLK0.ordinal() && clk.ordinal() <= SI5351_CLK3.ordinal()) {
            reg = SI5351_CLK3_0_DISABLE_STATE;
        } else if (clk.ordinal() >= SI5351_CLK4.ordinal() && clk.ordinal() <= SI5351_CLK7.ordinal()) {
            reg = SI5351_CLK7_4_DISABLE_STATE;
        } else return;

        reg_val = si5351_read(reg);

        if (clk.ordinal() >= SI5351_CLK0.ordinal() && clk.ordinal() <= SI5351_CLK3.ordinal()) {
            reg_val &= ~(0b11 << (clk.ordinal() * 2));
            reg_val |= dis_state.ordinal() << (clk.ordinal() * 2);
        } else if (clk.ordinal() >= SI5351_CLK4.ordinal() && clk.ordinal() <= SI5351_CLK7.ordinal()) {
            reg_val &= ~(0b11 << ((clk.ordinal() - 4) * 2));
            reg_val |= dis_state.ordinal() << ((clk.ordinal() - 4) * 2);
        }

        si5351_write(reg, reg_val);
    }

    /**
     * Use this function to enable or disable the clock fanout options
     * for individual clock outputs. If you intend to output the XO or
     * CLKIN on the clock outputs, enable this first.
     *
     * By default, only the Multisynth fanout is enabled at startup.
     *
     * fanout - Desired clock fanout
     *   (use the si5351_clock_fanout enum)
     * enable - Set to true to enable, false to disable
     */
    public void set_clock_fanout(si5351_clock_fanout fanout, boolean enable) throws IOException {
        byte reg_val = si5351_read(SI5351_FANOUT_ENABLE);

        switch (fanout) {
            case SI5351_FANOUT_CLKIN:
                if (enable) {
                    reg_val |= SI5351_CLKIN_ENABLE;
                } else {
                    reg_val &= ~(SI5351_CLKIN_ENABLE);
                }
                break;
            case SI5351_FANOUT_XO:
                if (enable) {
                    reg_val |= SI5351_XTAL_ENABLE;
                } else {
                    reg_val &= ~(SI5351_XTAL_ENABLE);
                }
                break;
            case SI5351_FANOUT_MS:
                if (enable) {
                    reg_val |= SI5351_MULTISYNTH_ENABLE;
                } else {
                    reg_val &= ~(SI5351_MULTISYNTH_ENABLE);
                }
                break;
        }

        si5351_write(SI5351_FANOUT_ENABLE, reg_val);
    }

    /**
     * Set the desired reference oscillator source for the given PLL.
     *
     * pll - Which PLL to use as the source
     *     (use the si5351_pll enum)
     * input - Which reference oscillator to use as PLL input
     *     (use the si5351_pll_input enum)
     */
    public void set_pll_input(si5351_pll pll, si5351_pll_input input) throws IOException {
        byte reg_val = si5351_read(SI5351_PLL_INPUT_SOURCE);

        // Clear the bits first
        //reg_val &= ~(SI5351_CLKIN_DIV_MASK);

        switch (pll) {
            case SI5351_PLLA:
                if (input == SI5351_PLL_INPUT_CLKIN) {
                    reg_val |= SI5351_PLLA_SOURCE;
                    reg_val |= clkin_div;
                    plla_ref_osc = SI5351_PLL_INPUT_CLKIN;
                } else {
                    reg_val &= ~(SI5351_PLLA_SOURCE);
                    plla_ref_osc = SI5351_PLL_INPUT_XO;
                }
                break;
            case SI5351_PLLB:
                if (input == SI5351_PLL_INPUT_CLKIN) {
                    reg_val |= SI5351_PLLB_SOURCE;
                    reg_val |= clkin_div;
                    pllb_ref_osc = SI5351_PLL_INPUT_CLKIN;
                } else {
                    reg_val &= ~(SI5351_PLLB_SOURCE);
                    pllb_ref_osc = SI5351_PLL_INPUT_XO;
                }
                break;
            default:
                return;
        }

        si5351_write(SI5351_PLL_INPUT_SOURCE, reg_val);

        set_pll(plla_freq, SI5351_PLLA);
        set_pll(pllb_freq, SI5351_PLLB);
    }

    /**
     * Set the parameters for the VCXO on the Si5351B.
     *
     * pll_freq - Desired PLL base frequency in Hz * 100
     * ppm - VCXO pL limit in ppm
     */
    public void set_vcxo(long pll_freq, int ppm) throws IOException {
        Si5351RegSet pll_reg = new Si5351RegSet();
        long vcxo_param;

        // Bounds check
        if (ppm < SI5351_VCXO_PL_MIN) {
            ppm = SI5351_VCXO_PL_MIN;
        }

        if (ppm > SI5351_VCXO_PL_MAX) {
            ppm = SI5351_VCXO_PL_MAX;
        }

        // Set PLLB params
        vcxo_param = pll_calc(SI5351_PLLB, pll_freq, pll_reg, ref_correction[pllb_ref_osc.ordinal()], true);

        // Derive the register values to write

        // Prepare an array for parameters to be written to
        byte[] params = new byte[20];
        int i = 0;
        byte temp;

        // Registers 26-27
        temp = (byte) ((pll_reg.p3 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p3 & 0xFF);
        params[i++] = temp;

        // Register 28
        temp = (byte) ((pll_reg.p1 >> 16) & 0x03);
        params[i++] = temp;

        // Registers 29-30
        temp = (byte) ((pll_reg.p1 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p1 & 0xFF);
        params[i++] = temp;

        // Register 31
        temp = (byte) ((pll_reg.p3 >> 12) & 0xF0);
        temp += (byte) ((pll_reg.p2 >> 16) & 0x0F);
        params[i++] = temp;

        // Registers 32-33
        temp = (byte) ((pll_reg.p2 >> 8) & 0xFF);
        params[i++] = temp;

        temp = (byte) (pll_reg.p2 & 0xFF);
        params[i++] = temp;

        // Write the parameters
        si5351_write_bulk(SI5351_PLLB_PARAMETERS, i, params);

        // Write the VCXO parameters
        vcxo_param = ((vcxo_param * ppm * SI5351_VCXO_MARGIN) / 100L) / 1000000L;

        temp = (byte) (vcxo_param & 0xFF);
        si5351_write(SI5351_VXCO_PARAMETERS_LOW, temp);

        temp = (byte) ((vcxo_param >> 8) & 0xFF);
        si5351_write(SI5351_VXCO_PARAMETERS_MID, temp);

        temp = (byte) ((vcxo_param >> 16) & 0x3F);
        si5351_write(SI5351_VXCO_PARAMETERS_HIGH, temp);
    }

    /**
     * Set the reference frequency value for the desired reference oscillator.
     *
     * ref_freq - Reference oscillator frequency in Hz
     * ref_osc - Which reference oscillator frequency to set
     *    (use the si5351_pll_input enum)
     */
    public void set_ref_freq(int ref_freq, si5351_pll_input ref_osc) {
        if (ref_freq <= 30_000_000) {
            xtal_freq[ref_osc.ordinal()] = ref_freq;
            if (ref_osc == SI5351_PLL_INPUT_CLKIN) {
                clkin_div = SI5351_CLKIN_DIV_1;
            }
        } else if (ref_freq <= 60_000_000) {
            xtal_freq[ref_osc.ordinal()] = ref_freq / 2;
            if (ref_osc == SI5351_PLL_INPUT_CLKIN) {
                clkin_div = SI5351_CLKIN_DIV_2;
            }
        } else if (ref_freq <= 100_000_000) {
            xtal_freq[ref_osc.ordinal()] = ref_freq / 4;
            if (ref_osc == SI5351_PLL_INPUT_CLKIN) {
                clkin_div = SI5351_CLKIN_DIV_4;
            }
        }
    }

    // Private functions

    private void si5351_write_bulk(int addr, int bytes, byte[] data) throws IOException {
        device.writeRegBuffer(addr, data, bytes);
    }

    private void si5351_write(int addr, byte data) throws IOException {
        device.writeRegByte(addr, data);
    }

    private byte si5351_read(int addr) throws IOException {
        return device.readRegByte(addr);
    }

    private long pll_calc(si5351_pll pll, long freq, Si5351RegSet reg, int correction, boolean vcxo) {
        long ref_freq;
        if (pll == SI5351_PLLA) {
            ref_freq = xtal_freq[plla_ref_osc.ordinal()] * SI5351_FREQ_MULT;
        } else {
            ref_freq = xtal_freq[pllb_ref_osc.ordinal()] * SI5351_FREQ_MULT;
        }
        //ref_freq = 15974400L * SI5351_FREQ_MULT;
        int a, b, c, p1, p2, p3;
        long lltmp; //, denom;

        // Factor calibration value into nominal crystal frequency
        // Measured in parts-per-billion

        ref_freq = ref_freq + (int) ((((((long) correction) << 31) / 1000000000L) * ref_freq) >> 31);

        // PLL bounds checking
        if (freq < SI5351_PLL_VCO_MIN * SI5351_FREQ_MULT) {
            freq = SI5351_PLL_VCO_MIN * SI5351_FREQ_MULT;
        }
        if (freq > SI5351_PLL_VCO_MAX * SI5351_FREQ_MULT) {
            freq = SI5351_PLL_VCO_MAX * SI5351_FREQ_MULT;
        }

        // Determine integer part of feedback equation
        a = (int) (freq / ref_freq);

        if (a < SI5351_PLL_A_MIN) {
            freq = ref_freq * SI5351_PLL_A_MIN;
        }
        if (a > SI5351_PLL_A_MAX) {
            freq = ref_freq * SI5351_PLL_A_MAX;
        }

        // Find best approximation for b/c = fVCO mod fIN
        // denom = 1000L * 1000L;
        // lltmp = freq % ref_freq;
        // lltmp *= denom;
        // do_div(lltmp, ref_freq);

        //b = (((long)(freq % ref_freq)) * RFRAC_DENOM) / ref_freq;
        if (vcxo) {
            b = (int) (((freq % ref_freq) * 1000000L) / ref_freq);
            c = 1000000;
        } else {
            b = (int) (((freq % ref_freq) * RFRAC_DENOM) / ref_freq);
            c = (b != 0) ? RFRAC_DENOM : 1;
        }

        // Calculate parameters
        p1 = 128 * a + ((128 * b) / c) - 512;
        p2 = 128 * b - c * ((128 * b) / c);
        p3 = c;

        // Recalculate frequency as fIN * (a + b/c)
        lltmp = ref_freq;
        lltmp *= b;
        lltmp /= c;
        freq = lltmp;
        freq += ref_freq * a;

        reg.p1 = p1;
        reg.p2 = p2;
        reg.p3 = p3;

        if (vcxo) {
            return 128 * a * 1000000L + b;
        } else {
            return freq;
        }
    }

    private long multisynth_calc(long freq, long pll_freq, Si5351RegSet reg) {
        long lltmp;
        int a, b, c, p1, p2, p3;
        int divby4 = 0;
        int ret_val = 0;

        // Multisynth bounds checking
        if (freq > SI5351_MULTISYNTH_MAX_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_MULTISYNTH_MAX_FREQ * SI5351_FREQ_MULT;
        }
        if (freq < SI5351_MULTISYNTH_MIN_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_MULTISYNTH_MIN_FREQ * SI5351_FREQ_MULT;
        }

        if (freq >= SI5351_MULTISYNTH_DIVBY4_FREQ * SI5351_FREQ_MULT) {
            divby4 = 1;
        }

        if (pll_freq == 0) {
            // Find largest integer divider for max
            // VCO frequency and given target frequency
            if (divby4 == 0) {
                lltmp = SI5351_PLL_VCO_MAX * SI5351_FREQ_MULT; // margin needed?
                lltmp /= freq;
                if (lltmp == 5) {
                    lltmp = 4;
                } else if (lltmp == 7) {
                    lltmp = 6;
                }
                a = (int) lltmp;
            } else {
                a = 4;
            }

            b = 0;
            c = 1;
            pll_freq = a * freq;
        } else {
            // Preset PLL, so return the actual freq for these params instead of PLL freq
            ret_val = 1;

            // Determine integer part of feedback equation
            a = (int) (pll_freq / freq);

            if (a < SI5351_MULTISYNTH_A_MIN) {
                freq = pll_freq / SI5351_MULTISYNTH_A_MIN;
            }
            if (a > SI5351_MULTISYNTH_A_MAX) {
                freq = pll_freq / SI5351_MULTISYNTH_A_MAX;
            }

            b = (int) ((pll_freq % freq * RFRAC_DENOM) / freq);
            c = (b != 0) ? RFRAC_DENOM : 1;
        }

        // Calculate parameters
        if (divby4 == 1) {
            p3 = 1;
            p2 = 0;
            p1 = 0;
        } else {
            p1 = 128 * a + ((128 * b) / c) - 512;
            p2 = 128 * b - c * ((128 * b) / c);
            p3 = c;
        }

        reg.p1 = p1;
        reg.p2 = p2;
        reg.p3 = p3;

        if (ret_val == 0) {
            return pll_freq;
        } else {
            return freq;
        }
    }

    private long multisynth67_calc(long freq, long pll_freq, Si5351RegSet reg) {
        int a;
        long lltmp;

        // Multisynth bounds checking
        if (freq > SI5351_MULTISYNTH67_MAX_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_MULTISYNTH67_MAX_FREQ * SI5351_FREQ_MULT;
        }
        if (freq < SI5351_MULTISYNTH_MIN_FREQ * SI5351_FREQ_MULT) {
            freq = SI5351_MULTISYNTH_MIN_FREQ * SI5351_FREQ_MULT;
        }

        if (pll_freq == 0) {
            // Find largest integer divider for max
            // VCO frequency and given target frequency
            lltmp = (SI5351_PLL_VCO_MAX * SI5351_FREQ_MULT) - 100000000L; // margin needed?
            lltmp /= freq;
            a = (int) lltmp;

            // Divisor has to be even
            if (a % 2 != 0) {
                a++;
            }

            // Divisor bounds check
            if (a < SI5351_MULTISYNTH_A_MIN) {
                a = SI5351_MULTISYNTH_A_MIN;
            }
            if (a > SI5351_MULTISYNTH67_A_MAX) {
                a = SI5351_MULTISYNTH67_A_MAX;
            }

            pll_freq = a * freq;

            // PLL bounds checking
            if (pll_freq > (SI5351_PLL_VCO_MAX * SI5351_FREQ_MULT)) {
                a -= 2;
                pll_freq = a * freq;
            } else if (pll_freq < (SI5351_PLL_VCO_MIN * SI5351_FREQ_MULT)) {
                a += 2;
                pll_freq = a * freq;
            }

            reg.p1 = a;
            reg.p2 = 0;
            reg.p3 = 0;
            return pll_freq;
        } else {
            // Multisynth frequency must be integer division of PLL
            if (pll_freq % freq != 0) {
                // No good
                return 0;
            } else {
                a = (int) (pll_freq / freq);

                // Division ratio bounds check
                if (a < SI5351_MULTISYNTH_A_MIN || a > SI5351_MULTISYNTH67_A_MAX) {
                    // No bueno
                    return 0;
                } else {
                    reg.p1 = a;
                    reg.p2 = 0;
                    reg.p3 = 0;
                    return 1;
                }
            }
        }
    }

    private void update_sys_status(Si5351Status status) throws IOException {
        byte reg_val = si5351_read(SI5351_DEVICE_STATUS);

        // Parse the register
        status.SYS_INIT = ((reg_val >> 7) & 0x01) != 0;
        status.LOL_B = ((reg_val >> 6) & 0x01) != 0;
        status.LOL_A = ((reg_val >> 5) & 0x01) != 0;
        status.LOS = ((reg_val >> 4) & 0x01) != 0;
        status.REVID = reg_val & 0x03;
    }

    private void update_int_status(Si5351IntStatus int_status) throws IOException {
        byte reg_val = si5351_read(SI5351_INTERRUPT_STATUS);

        // Parse the register
        int_status.SYS_INIT_STKY = ((reg_val >> 7) & 0x01) != 0;
        int_status.LOL_B_STKY = ((reg_val >> 6) & 0x01) != 0;
        int_status.LOL_A_STKY = ((reg_val >> 5) & 0x01) != 0;
        int_status.LOS_STKY = ((reg_val >> 4) & 0x01) != 0;
    }

    private void ms_div(si5351_clock clk, int r_div, boolean div_by_4) throws IOException {
        byte reg_val;
        int reg_addr = 0;

        switch (clk) {
            case SI5351_CLK0:
                reg_addr = SI5351_CLK0_PARAMETERS + 2;
                break;
            case SI5351_CLK1:
                reg_addr = SI5351_CLK1_PARAMETERS + 2;
                break;
            case SI5351_CLK2:
                reg_addr = SI5351_CLK2_PARAMETERS + 2;
                break;
            case SI5351_CLK3:
                reg_addr = SI5351_CLK3_PARAMETERS + 2;
                break;
            case SI5351_CLK4:
                reg_addr = SI5351_CLK4_PARAMETERS + 2;
                break;
            case SI5351_CLK5:
                reg_addr = SI5351_CLK5_PARAMETERS + 2;
                break;
            case SI5351_CLK6:
                reg_addr = SI5351_CLK6_7_OUTPUT_DIVIDER;
                break;
            case SI5351_CLK7:
                reg_addr = SI5351_CLK6_7_OUTPUT_DIVIDER;
                break;
        }

        reg_val = si5351_read(reg_addr);

        if (clk.ordinal() <= SI5351_CLK5.ordinal()) {
            // Clear the relevant bits
            reg_val &= ~(0x7c);

            if (!div_by_4) {
                reg_val &= ~(SI5351_OUTPUT_CLK_DIVBY4);
            } else {
                reg_val |= (SI5351_OUTPUT_CLK_DIVBY4);
            }

            reg_val |= (r_div << SI5351_OUTPUT_CLK_DIV_SHIFT);
        } else if (clk == SI5351_CLK6) {
            // Clear the relevant bits
            reg_val &= ~(0x07);

            reg_val |= r_div;
        } else if (clk == SI5351_CLK7) {
            // Clear the relevant bits
            reg_val &= ~(0x70);

            reg_val |= (r_div << SI5351_OUTPUT_CLK_DIV_SHIFT);
        }

        si5351_write(reg_addr, reg_val);
    }

    private int select_r_div(long[] freq) {
        int r_div = SI5351_OUTPUT_CLK_DIV_1;

        // Choose the correct R divider
        if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 2)) {
            r_div = SI5351_OUTPUT_CLK_DIV_128;
            freq[0] *= 128L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 2) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 4)) {
            r_div = SI5351_OUTPUT_CLK_DIV_64;
            freq[0] *= 64L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 4) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 8)) {
            r_div = SI5351_OUTPUT_CLK_DIV_32;
            freq[0] *= 32L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 8) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 16)) {
            r_div = SI5351_OUTPUT_CLK_DIV_16;
            freq[0] *= 16L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 16) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 32)) {
            r_div = SI5351_OUTPUT_CLK_DIV_8;
            freq[0] *= 8L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 32) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 64)) {
            r_div = SI5351_OUTPUT_CLK_DIV_4;
            freq[0] *= 4L;
        } else if ((freq[0] >= SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 64) && (freq[0] < SI5351_CLKOUT_MIN_FREQ * SI5351_FREQ_MULT * 128)) {
            r_div = SI5351_OUTPUT_CLK_DIV_2;
            freq[0] *= 2L;
        }

        return r_div;
    }

    private int select_r_div_ms67(long[] freq) {
        int r_div = SI5351_OUTPUT_CLK_DIV_1;

        // Choose the correct R divider
        if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 2)) {
            r_div = SI5351_OUTPUT_CLK_DIV_128;
            freq[0] *= 128L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 2) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 4)) {
            r_div = SI5351_OUTPUT_CLK_DIV_64;
            freq[0] *= 64L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 4) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 8)) {
            r_div = SI5351_OUTPUT_CLK_DIV_32;
            freq[0] *= 32L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 8) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 16)) {
            r_div = SI5351_OUTPUT_CLK_DIV_16;
            freq[0] *= 16L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 16) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 32)) {
            r_div = SI5351_OUTPUT_CLK_DIV_8;
            freq[0] *= 8L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 32) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 64)) {
            r_div = SI5351_OUTPUT_CLK_DIV_4;
            freq[0] *= 4L;
        } else if ((freq[0] >= SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 64) && (freq[0] < SI5351_CLKOUT67_MIN_FREQ * SI5351_FREQ_MULT * 128)) {
            r_div = SI5351_OUTPUT_CLK_DIV_2;
            freq[0] *= 2L;
        }

        return r_div;
    }
}
