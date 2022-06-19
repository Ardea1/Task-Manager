package com.example.taskmanager.bottomSheetFragment;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.taskmanager.R;
import com.example.taskmanager.activity.MainActivity;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.database.DatabaseClient;
import com.example.taskmanager.model.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


// Класс отвечает за отображение задачи, задач в нём
public class ShowTask extends BottomSheetDialogFragment {

    int taskId;
    boolean isEdit;
    Task task;
    CreateTaskBottom.setRefreshListener setRefreshListener;
    MainActivity activity;
    Unbinder unbinder;

    @BindView(R.id.dayTask)
    TextView day;
    @BindView(R.id.dateTask)
    TextView date;
    @BindView(R.id.monthTask)
    TextView month;
    @BindView(R.id.titleTask)
    TextView title;
    @BindView(R.id.descriptionTask)
    TextView description;
    @BindView(R.id.timeTask)
    TextView time;
    @BindView(R.id.category)
    TextView category;

    @BindView(R.id.back)
    ImageView back;

    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date1 = null;
    String outputDateString = null;

    // Блок с заданием будет отображён внизу страницы
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public void setTaskId(int taskId, boolean isEdit, CreateTaskBottom.setRefreshListener setRefreshListener, MainActivity activity) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.activity = activity;
        this.setRefreshListener = setRefreshListener;
    }

    // Dialog — это небольшое окно, предлагающее пользователю принять
    // решение или ввести дополнительную информацию. Диалоговое окно
    // не заполняет экран и обычно используется для модальных событий,
    // которые требуют от пользователей выполнения действия, прежде чем они смогут продолжить.

    // @RequiresApi Указывает, что аннотированный элемент следует вызывать
    // только на данном уровне API или выше.
    // @SuppressLint("ClickableViewAccessibility") для подавления предупреждения
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_task, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        showTaskFromId();
        back.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showTaskFromId() {
        class showTaskFromId extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                task = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .dataBaseAction().selectDataFromAnId(taskId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setDataInUI();
            }
        }
        showTaskFromId st = new showTaskFromId();
        st.execute();
    }

    private void setDataInUI() {

        try {
        date1 = inputDateFormat.parse(task.getDate());
        outputDateString = dateFormat.format(date1);

        title.setText(task.getTaskTitle());
        description.setText(task.getTaskDescrption());
        date.setText(task.getDate());
        time.setText(task.getLastAlarm());
        category.setText(task.getCategory());

        String[] items1 = outputDateString.split(" ");
        String day1 = items1[0];
        String month1 = items1[2];

        day.setText(day1);
        month.setText(month1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface setRefreshListener {
        void refresh();
    }

}
