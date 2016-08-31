package com.example.cbess.firebasecrud;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Handles task UI data.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskItem> tasks;
    private TaskAdapterListener listener;
    private String editTextHint = "";

    class TaskViewHolder extends RecyclerView.ViewHolder {
        protected EditText editText;
        protected Button deleteButton;

        public TaskViewHolder(View taskView) {
            super(taskView);

            editText = (EditText) taskView.findViewById(R.id.edit_text);
            deleteButton = (Button) taskView.findViewById(R.id.delete_button);
        }
    }

    public TaskAdapter(List<TaskItem> tasks, TaskAdapterListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        TaskItem item = tasks.get(position);

        holder.deleteButton.setEnabled((item.getUid() != null));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteButtonClick(TaskAdapter.this, holder.getAdapterPosition());
            }
        });

        holder.editText.setHint(editTextHint);
        holder.editText.setText(item.getName());
        holder.editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                TaskItem item = tasks.get(holder.getAdapterPosition());
                item.setName(textView.getText().toString());

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    listener.onDoneKeyClick(TaskAdapter.this, holder.getAdapterPosition());
                }

                // return false to dismiss keyboard, true to keep keyboard visible
                return false;
            }
        });
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row_view, parent, false);
        return new TaskViewHolder(view);
    }

    public void setEditTextHint(String editTextHint) {
        this.editTextHint = editTextHint;
    }

    interface TaskAdapterListener {
        void onDeleteButtonClick(TaskAdapter adapter, int position);
        void onDoneKeyClick(TaskAdapter adapter, int position);
    }
}

