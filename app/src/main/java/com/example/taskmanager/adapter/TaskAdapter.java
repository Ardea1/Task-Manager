package com.example.taskmanager.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taskmanager.R;
import com.example.taskmanager.activity.MainActivity;
import com.example.taskmanager.bottomSheetFragment.CreateTaskBottom;
import com.example.taskmanager.bottomSheetFragment.ShowTask;
import com.example.taskmanager.database.DatabaseClient;
import com.example.taskmanager.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private MainActivity context;
    // LayoutInflater – это класс, который умеет из содержимого layout-файла
    // создать View-элемент. Метод который это делает называется inflate.
    private LayoutInflater inflater;
    private List<Task> taskList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    CreateTaskBottom.setRefreshListener setRefreshListener;
    ShowTask.setRefreshListener setRefreshListener2;

    public TaskAdapter(MainActivity context, List<Task> taskList,  CreateTaskBottom.setRefreshListener setRefreshListener, ShowTask.setRefreshListener setRefreshListener2) {
        this.context = context;
        this.taskList = taskList;
        this.setRefreshListener = setRefreshListener;
        this.setRefreshListener2 = setRefreshListener2;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Данный метод вызывается LayoutManager‘ом, чтобы создать объекты
    // viewHolder и передать им макет, по которому будут отображаться элементы списка
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @NonNull
    public TaskViewHolder onCreateViewHolder2(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.fragment_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    // Данный метод вызывается LayoutManager‘ом, чтобы привязать
    // к объекту viewHolder данные, которые он должен отображать
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTaskTitle());
        holder.description.setText(task.getTaskDescrption());
        holder.time.setText(task.getLastAlarm());
        holder.status.setText(task.isComplete() ? "ВЫПОЛНЕНО" : "В ПРОЦЕССЕ");
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));
        holder.title.setOnClickListener(view -> showTask(view, position));

        try {
            date = inputDateFormat.parse(task.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // PopupMenu (также называемое контекстным меню) — это меню, привязанное к определенному представлению
    public void showPopUpMenu(View view, int position) {
        final Task task = taskList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                deleteTaskFromId(task.getTaskId(), position);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuUpdate:
                    CreateTaskBottom createTaskBottomSheetFragment = new CreateTaskBottom();
                    createTaskBottomSheetFragment.setTaskId(task.getTaskId(), true, context, context);
                    createTaskBottomSheetFragment.show(context.getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
                    break;
                case R.id.menuComplete:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                            setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task.getTaskId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    // Показывает подробности о задаче
    public void showTask(View view, int position) {
        final Task task = taskList.get(position);
        ShowTask showTaskFragment = new ShowTask();
        showTaskFragment.setTaskId(task.getTaskId(), true, context, context);
        showTaskFragment.show(context.getSupportFragmentManager(), showTaskFragment.getTag());
    }

    // Метод, показывающий диалог о завершении задачи
    public void showCompleteDialog(int taskId, int position) {
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_comleted);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            deleteTaskFromId(taskId, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    // Метод для удаления задачи из БД
    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .dataBaseAction()
                        .deleteTaskFromId(taskId);

                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
                setRefreshListener.refresh();
                setRefreshListener2.refresh();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    // Возвращает общее количество элементов в списке
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;

        TaskViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}