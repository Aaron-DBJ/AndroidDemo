package com.aaron.hookpractice.spi;

import android.util.Log;

import com.aaron.dibinder.IService;
import com.aaron.diview.ServiceLoaderInterface;

/**
 * @author dbj
 * @date 1/24/24
 * @description
 */
@ServiceLoaderInterface(key = AudioService.TAG, interfaceClass = IService.class)
public class AudioService implements IService {
    public static final String TAG = "AudioService";

    public void audio() {
        Log.d(TAG, "audio: audio service");
    }
}
