package com.example.myapplication.ui.calc_sil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.StrengthRecord;
import com.example.myapplication.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalcsilFragment extends Fragment {

    private static final String TAG = "CalcsilFragment";

    private String currentExercise = "";
    private int currentOneRepMax = 0;

    private EditText weightEditText;
    private EditText repsEditText;
    private EditText exerciseEditText;
    private TextView resultTextView;
    private Button calculateButton;
    private Button saveButton;
    private Button viewRecordsButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_calc_sil, container, false);

        initializeUIComponents(rootView);

        setupEventListeners();

        return rootView;
    }
    private void initializeUIComponents(View rootView)
    {
            weightEditText = rootView.findViewById(R.id.edittext_calc_sil_weight);
            repsEditText = rootView.findViewById(R.id.edittext_calc_sil_repeats);
            exerciseEditText = rootView.findViewById(R.id.edittext_exercise_name);

            resultTextView = rootView.findViewById(R.id.text_calc_sil_1pm);

            calculateButton = rootView.findViewById(R.id.button_calc_sil);
            saveButton = rootView.findViewById(R.id.button_save_record);
            viewRecordsButton = rootView.findViewById(R.id.button_view_records);

            if (saveButton != null)
            {
                saveButton.setEnabled(false);
            }
    }

    private void setupEventListeners()
    {
            if (calculateButton != null)
            {
                calculateButton.setOnClickListener(v -> calculateOneRepMax());
            }

            if (saveButton != null)
            {
                saveButton.setOnClickListener(v -> saveCurrentRecord());
            }

            if (viewRecordsButton != null)
            {
                viewRecordsButton.setOnClickListener(v -> navigateToRecordsView());
            }
    }
    private void calculateOneRepMax()
    {
            currentExercise = exerciseEditText.getText().toString().trim();
            if (currentExercise.isEmpty())
            {
                showToast("Введите название упражнения");
                return;
            }

            String weightStr = weightEditText.getText().toString();
            String repsStr = repsEditText.getText().toString();

            if (weightStr.isEmpty() || repsStr.isEmpty())
            {
                showToast("Введите вес и количество повторений");
                return;
            }

            int weight = Integer.parseInt(weightStr);
            int reps = Integer.parseInt(repsStr);

            if (weight <= 0)
            {
                showToast("Вес должен быть положительным числом");
                return;
            }

            if (reps <= 0)
            {
                showToast("Количество повторений должно быть положительным числом");
                return;
            }

            if (reps >= 37)
            {
                showToast("Формула не работает для более чем 36 повторений");
                return;
            }

            String resultText = calculateOneRepMaxText(weight, reps);

            resultTextView.setText(resultText);
            saveButton.setEnabled(true);

    }

    private String calculateOneRepMaxText(int weight, int reps)
    {
        StringBuilder resultText = new StringBuilder();

        if (reps <= 0 || reps >= 37)
        {
            showToast("Поддерживается от 1 до 36 повторений");
            return "";
        }

        currentOneRepMax = (int)(weight * (36.0 / (37.0 - reps)));

        int maxRepsToShow = Math.min(9, 36 - reps);
        for (int i = 1; i <= maxRepsToShow; i++)
        {
            int maxForReps;
            if (i == reps)
            {
                maxForReps = weight;
            }
            else
            {
                maxForReps = (int)(currentOneRepMax * (37.0 - i) / 36.0);
            }
            resultText.append("Ваш ").append(i).append("ПМ равен ").append(maxForReps).append("\n");
        }

        return resultText.toString();
    }

    private void saveCurrentRecord()
    {
            if (currentExercise == null || currentExercise.isEmpty())
            {
                currentExercise = exerciseEditText.getText().toString().trim();
                if (currentExercise.isEmpty())
                {
                    showToast("Введите название упражнения");
                    return;
                }
            }

            if (currentOneRepMax <= 0)
            {
                showToast("Сначала рассчитайте одноповторный максимум");
                return;
            }

            int enteredWeight = 0;
            int enteredReps = 0;
            enteredWeight = Integer.parseInt(weightEditText.getText().toString());
            enteredReps = Integer.parseInt(repsEditText.getText().toString());

            StrengthRecord record = new StrengthRecord(
                    currentExercise,
                    currentOneRepMax,
                    enteredWeight,
                    enteredReps
            );

            saveStrengthRecord(record);
    }

    private void saveStrengthRecord(StrengthRecord record)
    {
        showToast("Сохранение...");

        saveStrengthRecordLocally(record);

        SupabaseClient.getInstance().checkColumnsExistence(new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                SupabaseClient.getInstance().addStrengthRecord(record, new SupabaseClient.SupabaseCallback<Boolean>()
                {
                    @Override
                    public void onSuccess(Boolean result)
                    {
                        Log.d(TAG, "Силовой показатель успешно сохранен в Supabase");
                        runOnUiThread(() ->
                        {
                            showToast("Силовой показатель успешно сохранен");
                        });
                    }

                    @Override
                    public void onError(String error)
                    {
                        Log.e(TAG, "Ошибка при сохранении в Supabase: " + error);
                        runOnUiThread(() ->
                        {
                            showToast("Ошибка сохранения в облаке: " + error);
                            showToast("Данные сохранены локально");
                        });
                    }
                });
            }

            @Override
            public void onError(String error)
            {
                runOnUiThread(() -> {
                    showToast("Ошибка проверки колонок: " + error);
                    showToast("Данные сохранены только локально");
                });
            }
        });
    }

    private void saveStrengthRecordLocally(StrengthRecord record)
    {
        try
        {
            SharedPreferences prefs = requireActivity().getSharedPreferences("strength_records", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String recordsJson = prefs.getString("records", "[]");
            JSONArray recordsArray = new JSONArray(recordsJson);

            JSONObject recordJson = new JSONObject();
            recordJson.put("id", System.currentTimeMillis());
            recordJson.put("exercise", record.getExercise());
            recordJson.put("one_rep_max", record.getOneRepMax());
            recordJson.put("entered_weight", record.getEnteredWeight());
            recordJson.put("entered_reps", record.getEnteredReps());
            recordJson.put("date", record.getDate());
            recordsArray.put(recordJson);

            editor.putString("records", recordsArray.toString());
            editor.apply();

            Log.d(TAG, "Силовой показатель успешно сохранен локально: " + recordJson.toString());

        }
        catch (Exception e)
        {
            Log.e(TAG, "Ошибка при локальном сохранении", e);
        }
    }
    private void navigateToRecordsView()
    {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new com.example.myapplication.ui.database.DatabaseFragment())
                    .addToBackStack(null)
                    .commit();
    }

    private void showToast(String message)
    {
        if (getContext() != null)
        {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    private void runOnUiThread(Runnable runnable)
    {
        if (getActivity() != null)
        {
            getActivity().runOnUiThread(runnable);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }
}