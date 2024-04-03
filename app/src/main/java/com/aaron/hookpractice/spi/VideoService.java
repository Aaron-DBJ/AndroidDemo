package com.aaron.hookpractice.spi;

import android.util.Log;

import com.aaron.dibinder.IService;
import com.aaron.diview.ServiceLoaderInterface;

/**
 * @author dbj
 * @date 1/24/24
 * @description
 */
@ServiceLoaderInterface(key = VideoService.TAG, interfaceClass = IService.class)
public class VideoService implements IService {
    public static final String TAG = "VideoService";

    public void video() {
        Log.d(TAG, "video: video service");
    }
}
