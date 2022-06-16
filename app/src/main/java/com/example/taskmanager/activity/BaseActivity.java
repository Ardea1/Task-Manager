package com.example.taskmanager.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

// AppCompatActivity применяется для обратной совместимости в плане дизайна
// Данный класс содержит методы обратного вызова жизненного цикла активити
public class BaseActivity extends AppCompatActivity {

    // Данный метод содержит действия, которые будут выполнены,
    // при вызове родительского метода super.onCreate()
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // onResume() — метод жизненного цикла Activity, который уведомляет,
    // что необходимый контент был добавлен в DecorView, но будет отображен
    // только тогда, когда ViewRootlmpI установит эту view и выполнит
    // обход вызовов методов View для отрисовки.
    @Override
    public void onResume() {
        super.onResume();
    }

    // Функция attachBaseContext класса ContextWrapper заключается в том,
    // чтобы контекст прикреплялся только один раз.
    // Класс ContextWrapper используется для переноса любого контекста
    // (контекст приложения, контекст действия или базовый контекст)
    // в исходный контекст, не нарушая его.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}