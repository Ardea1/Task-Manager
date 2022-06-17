package com.example.taskmanager.activity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Библиотека Glide является ближайшим конкурентом другой популярной
// библиотеке Picasso и также предназначена для асинхронной подгрузки
// изображений из сети, ресурсов или файловой системы,
// их кэширования и отображения.
import com.bumptech.glide.Glide;
import com.example.taskmanager.R;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.bottomSheetFragment.CreateTaskBottom;
import com.example.taskmanager.bottomSheetFragment.ShowCalendarBottom;
import com.example.taskmanager.bottomSheetFragment.ShowTask;
import com.example.taskmanager.broadcastReciever.AlarmBroadcastReceiver;
import com.example.taskmanager.database.DatabaseClient;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

// ButterKnife — инструмент байндинга (с англ. «связывание»),
// который использует аннотации для генерации шаблонного кода.
// Основная задача библиотеки состоит в том, чтобы избавить
// нас от избыточного кода, множественного использования
// findViewById(R.id.some_view)при работе с View.
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements CreateTaskBottom.setRefreshListener, ShowTask.setRefreshListener {

    @BindView(R.id.taskRecycler)
    RecyclerView taskRecycler;
    @BindView(R.id.addTask)
    TextView addTask;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    @BindView(R.id.noDataImage)
    ImageView noDataImage;
    @BindView(R.id.calendar)
    ImageView calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Используем конструктор ComponentName для ссылки
        // на компоненты в нашем собственном приложении
        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        // Загружаем картинку с надписью "Hello"
        Glide.with(getApplicationContext()).load(R.drawable.first_note).into(noDataImage);

        //  Присваивает себе обработчика с помощью метода setOnClickListener
        //  (View.OnClickListener l)
        addTask.setOnClickListener(view -> {
            CreateTaskBottom createTaskBottomSheetFragment = new CreateTaskBottom();
            createTaskBottomSheetFragment.setTaskId(0, false, this, MainActivity.this);
            createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        calendar.setOnClickListener(view -> {
            ShowCalendarBottom showCalendarViewBottomSheet = new ShowCalendarBottom();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });

        getSavedTasks();
    }

    // Метод который создает адаптер и сетит туда данные
    // из нашей БД с помощью контроллера.
    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(this, tasks, this, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    // Получает сохранённые задачи
    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                // Определяет видно ли изображение на главном экране
                noDataImage.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}