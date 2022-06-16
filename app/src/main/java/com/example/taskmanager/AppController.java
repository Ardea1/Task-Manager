package com.example.taskmanager;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import io.github.inflationx.viewpump.ViewPump;

// Класс контроллера приложения предоставляет нам возможность выполнять запросы GET и POST
// в очереди с назначенными им параметрами
public class AppController extends Application implements ComponentCallbacks2 {

    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ViewPump.init(
                ViewPump.builder().build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}