package com.example.myapplication.ui.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.StrengthRecord;

import java.util.List;

public class StrengthRecordsAdapter extends RecyclerView.Adapter<StrengthRecordsAdapter.RecordViewHolder>
{
    private List<StrengthRecord> records;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener
    {
        void onDeleteClick(int recordId);
    }

    public StrengthRecordsAdapter(List<StrengthRecord> records, OnDeleteClickListener listener)
    {
        this.records = records;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_strength_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position)
    {
        StrengthRecord record = records.get(position);

        holder.exerciseTextView.setText(record.getExercise());

        String weightInfo = "1ПМ: " + record.getOneRepMax() + " кг";

        try
        {
            if (record.getEnteredWeight() > 0 && record.getEnteredReps() > 0)
            {
                weightInfo += " (Вес: " + record.getEnteredWeight() + " кг × " + record.getEnteredReps() + " повт.)";
            }
        }
        catch (Exception e)
        {

        }

        holder.oneRepMaxTextView.setText(weightInfo);
        holder.dateTextView.setText(record.getDate());

        if (record.isLocalOnly())
        {
            holder.dateTextView.setText(record.getDate() + " (Локально)");
        }
        else
        {
            holder.dateTextView.setText(record.getDate());
        }

        holder.deleteButton.setOnClickListener(v ->
        {
            if (onDeleteClickListener != null)
            {
                onDeleteClickListener.onDeleteClick(record.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setRecords(List<StrengthRecord> records)
    {
        this.records = records;
        notifyDataSetChanged();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder
    {
        public TextView exerciseTextView;
        public TextView oneRepMaxTextView;
        public TextView dateTextView;
        public Button deleteButton;

        public RecordViewHolder(@NonNull View itemView)
        {
            super(itemView);
            exerciseTextView = itemView.findViewById(R.id.exerciseTextView);
            oneRepMaxTextView = itemView.findViewById(R.id.oneRepMaxTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}