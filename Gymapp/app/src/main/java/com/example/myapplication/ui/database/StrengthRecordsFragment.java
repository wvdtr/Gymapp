package com.example.myapplication.ui.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.StrengthRecord;
import com.example.myapplication.SupabaseClient;
import com.example.myapplication.databinding.FragmentStrengthRecordsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StrengthRecordsFragment extends Fragment implements StrengthRecordsAdapter.OnDeleteClickListener
{

    private static final String TAG = "StrengthRecordsFragment";

    private FragmentStrengthRecordsBinding binding;
    private RecyclerView recyclerView;
    private StrengthRecordsAdapter adapter;
    private List<StrengthRecord> recordsList = new ArrayList<>();
    private SupabaseClient supabaseClient;
    private boolean showingLocalData = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentStrengthRecordsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        supabaseClient = SupabaseClient.getInstance();

        recyclerView = binding.strengthRecordsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StrengthRecordsAdapter(recordsList, this);
        recyclerView.setAdapter(adapter);

        binding.noRecordsText.setOnClickListener(v -> showAddRecordDialog());

        binding.toggleDataSourceButton.setOnClickListener(v ->
        {
            if (!showingLocalData)
            {
                binding.toggleDataSourceButton.setText("Показать облачные данные");
                showingLocalData = true;
                loadRecordsLocally();
            }
            else
            {
                binding.toggleDataSourceButton.setText("Показать локальные данные");
                showingLocalData = false;
                loadRecords();
            }
        });
        loadRecords();
        return root;
    }
    private void loadRecords()
    {
        binding.strengthRecordsProgressBar.setVisibility(View.VISIBLE);

        supabaseClient.checkColumnsExistence(new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                supabaseClient.getStrengthRecords(new SupabaseClient.SupabaseCallback<List<StrengthRecord>>()
                {
                    @Override
                    public void onSuccess(List<StrengthRecord> result)
                    {
                        requireActivity().runOnUiThread(() ->
                        {
                            binding.strengthRecordsProgressBar.setVisibility(View.GONE);
                            recordsList.clear();
                            recordsList.addAll(result);
                            adapter.notifyDataSetChanged();

                            if (recordsList.isEmpty())
                            {
                                binding.noRecordsText.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                binding.noRecordsText.setVisibility(View.GONE);
                            }

                            loadAndMergeLocalRecords();
                        });
                    }

                    @Override
                    public void onError(String error)
                    {
                        Log.e(TAG, "Ошибка загрузки из Supabase: " + error);
                        loadRecordsLocally();
                    }
                });
            }
            @Override
            public void onError(String error)
            {
                loadRecordsLocally();
            }
        });
    }

    private void loadRecordsLocally()
    {
        try
        {
            binding.strengthRecordsProgressBar.setVisibility(View.VISIBLE);

            SharedPreferences prefs = requireActivity().getSharedPreferences("strength_records", Context.MODE_PRIVATE);
            String recordsJson = prefs.getString("records", "[]");
            JSONArray recordsArray = new JSONArray(recordsJson);
            List<StrengthRecord> localRecords = new ArrayList<>();

            for (int i = 0; i < recordsArray.length(); i++)
            {
                JSONObject recordJson = recordsArray.getJSONObject(i);

                int id = recordJson.optInt("id", (int) System.currentTimeMillis() + i);
                String exercise = recordJson.optString("exercise", "");
                int oneRepMax = recordJson.optInt("one_rep_max", 0);
                String date = recordJson.optString("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                int enteredWeight = recordJson.optInt("entered_weight", 0);
                int enteredReps = recordJson.optInt("entered_reps", 0);

                StrengthRecord record = new StrengthRecord(id, exercise, oneRepMax, enteredWeight, enteredReps, date);
                record.setLocalOnly(true);
                localRecords.add(record);
            }

            requireActivity().runOnUiThread(() ->
            {
                binding.strengthRecordsProgressBar.setVisibility(View.GONE);
                recordsList.clear();
                recordsList.addAll(localRecords);
                adapter.notifyDataSetChanged();

                if (recordsList.isEmpty()) {
                    binding.noRecordsText.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.noRecordsText.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), "Загружены локальные данные", Toast.LENGTH_SHORT).show();
            });

        }
        catch (JSONException e)
        {
            Log.e(TAG, "Ошибка при загрузке локальных данных: " + e.getMessage(), e);
            requireActivity().runOnUiThread(() ->
            {
                binding.strengthRecordsProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка при загрузке локальных данных", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void loadAndMergeLocalRecords()
    {
        try
        {
            SharedPreferences prefs = requireActivity().getSharedPreferences("strength_records", Context.MODE_PRIVATE);
            String recordsJson = prefs.getString("records", "[]");
            JSONArray recordsArray = new JSONArray(recordsJson);

            Set<Integer> existingIds = new HashSet<>();
            for (StrengthRecord record : recordsList)
            {
                existingIds.add(record.getId());
            }
            for (int i = 0; i < recordsArray.length(); i++)
            {
                JSONObject recordJson = recordsArray.getJSONObject(i);

                int id = recordJson.optInt("id", (int) System.currentTimeMillis() + i);

                if (!existingIds.contains(id))
                {
                    String exercise = recordJson.optString("exercise", "");
                    int oneRepMax = recordJson.optInt("one_rep_max", 0);
                    String date = recordJson.optString("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                    int enteredWeight = recordJson.optInt("entered_weight", 0);
                    int enteredReps = recordJson.optInt("entered_reps", 0);

                    StrengthRecord record = new StrengthRecord(id, exercise, oneRepMax, enteredWeight, enteredReps, date);
                    record.setLocalOnly(true);
                    recordsList.add(record);
                    existingIds.add(id);
                }
            }
            adapter.notifyDataSetChanged();

            if (recordsList.isEmpty())
            {
                binding.noRecordsText.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.noRecordsText.setVisibility(View.GONE);
            }

        }
        catch (JSONException e)
        {}
    }

    @Override
    public void onDeleteClick(int recordId)
    {
        deleteRecord(recordId);
    }

    private void deleteRecord(int recordId)
    {
        binding.strengthRecordsProgressBar.setVisibility(View.VISIBLE);

        deleteRecordLocally(recordId);

        supabaseClient.deleteStrengthRecord(recordId, new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.strengthRecordsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Запись успешно удалена", Toast.LENGTH_SHORT).show();

                    if (showingLocalData)
                    {
                        loadRecordsLocally();
                    }
                    else
                    {
                        loadRecords();
                    }
                });
            }

            @Override
            public void onError(String error)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.strengthRecordsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка удаления из облака: " + error, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Запись удалена локально", Toast.LENGTH_SHORT).show();

                    if (showingLocalData)
                    {
                        loadRecordsLocally();
                    }
                    else
                    {
                        loadRecords();
                    }
                });
            }
        });
    }

    private void deleteRecordLocally(int recordId)
    {
        try
        {
            SharedPreferences prefs = requireActivity().getSharedPreferences("strength_records", Context.MODE_PRIVATE);
            String recordsJson = prefs.getString("records", "[]");
            JSONArray recordsArray = new JSONArray(recordsJson);
            JSONArray newRecordsArray = new JSONArray();

            for (int i = 0; i < recordsArray.length(); i++)
            {
                JSONObject recordJson = recordsArray.getJSONObject(i);
                int id = recordJson.optInt("id", 0);

                if (id != recordId)
                {
                    newRecordsArray.put(recordJson);
                }
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("records", newRecordsArray.toString());
            editor.apply();
        }
        catch (JSONException e)
        {}
    }

    private void showAddRecordDialog()
    {}

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}