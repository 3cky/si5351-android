# si5351-android

This is an Android library for the Si5351 series of clock generator ICs from Silicon Labs, ported to Java from the [C++ library for Arduino](https://github.com/etherkit/Si5351Arduino). It is using [usb-i2c-android](https://github.com/3cky/usb-i2c-android) library for connectivity. All interfaces are almost the same as those in the Arduino library. Refer to the Arduino project for the complete reference of the interfaces.

## Usage

### Gradle

Add the dependency to your `build.gradle`:

```gradle
allprojects {
    repositories {
       maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.3cky:si5351-android:1.0.0'
}
```

### Java

```java
import com.github.ykc3.android.si5351.Si5351;
import static com.github.ykc3.android.si5351.Si5351.SI5351_CRYSTAL_LOAD_8PF;
import static com.github.ykc3.android.si5351.Si5351.si5351_clock.SI5351_CLK0;
// Get the USB I2C adapter
UsbI2cAdapter i2cAdapter = usbI2cManager.getAdapter(usbDevice)
// Create Si5351 object
Si5351 si5351 = new Si5351(i2cAdapter);
// Initialize Si5351 using default reference crystal oscillator frequency of 25 MHz 
si5351.init(SI5351_CRYSTAL_LOAD_8PF, 0, 0);
// Set CLK0 output to 14 MHz
si5351.set_freq(1400000000L, SI5351_CLK0);
```

See also included [example project](app).
