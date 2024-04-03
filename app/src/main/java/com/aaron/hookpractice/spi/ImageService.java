package com.aaron.hookpractice.spi;

import android.util.Log;
import android.widget.Toast;

import com.aaron.dibinder.IService;
import com.aaron.diview.ServiceLoaderInterface;

/**
 * @author dbj
 * @date 1/24/24
 * @description
 */
@ServiceLoaderInterface(key = ImageService.TAG, interfaceClass = IService.class)
public class ImageService implements IService {
    public static final String TAG = "ImageService";

    public void image() {
        Log.d(TAG, "image: image service");
    }
}
