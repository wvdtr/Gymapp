package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseClient
{
    private static final String TAG = "SupabaseClient";
    private static final String SUPABASE_URL = "https://svkqpgarlxrodaczltwd.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN2a3FwZ2FybHhyb2RhY3psdHdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5MzY0OTksImV4cCI6MjA2NDUxMjQ5OX0.FOkMInTp4gk4N_iaPiY77mIxNKzO_Iyb4cWA94JYZEE";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static SupabaseClient instance;
    private final OkHttpClient client;

    private boolean hasEnteredWeightColumn = false;
    private boolean hasEnteredRepsColumn = false;
    private boolean checkedColumns = false;

    private SupabaseClient() {
        client = new OkHttpClient();
    }

    public static synchronized SupabaseClient getInstance()
    {
        if (instance == null)
        {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public interface SupabaseCallback<T>
    {
        void onSuccess(T result);
        void onError(String error);
    }

    public void checkColumnsExistence(final SupabaseCallback<Boolean> callback)
    {
        if (checkedColumns)
        {
            callback.onSuccess(true);
            return;
        }

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/strength_records?select=*&limit=1")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (!response.isSuccessful())
                {
                    callback.onError("Ошибка при проверке колонок: " + response.code());
                    return;
                }

                try
                {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (jsonArray.length() > 0)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        hasEnteredWeightColumn = jsonObject.has("entered_weight");
                        hasEnteredRepsColumn = jsonObject.has("entered_reps");
                    }

                    checkedColumns = true;
                    callback.onSuccess(true);

                }
                catch (JSONException e)
                {
                    callback.onError("Ошибка при парсинге данных: " + e.getMessage());
                }
            }
        });
    }

    public void getStrengthRecords(final SupabaseCallback<List<StrengthRecord>> callback)
    {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/strength_records?select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (!response.isSuccessful())
                {
                    callback.onError("Ошибка при получении силовых показателей: " + response.code());
                    return;
                }

                try
                {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    List<StrengthRecord> records = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String exercise = jsonObject.getString("exercise");
                        int oneRepMax = jsonObject.getInt("one_rep_max");
                        String date = jsonObject.getString("date");

                        int enteredWeight = 0;
                        int enteredReps = 0;

                        if (jsonObject.has("entered_weight") && !jsonObject.isNull("entered_weight"))
                        {
                            enteredWeight = jsonObject.getInt("entered_weight");
                            hasEnteredWeightColumn = true;
                        }
                        if (jsonObject.has("entered_reps") && !jsonObject.isNull("entered_reps"))
                        {
                            enteredReps = jsonObject.getInt("entered_reps");
                            hasEnteredRepsColumn = true;
                        }

                        StrengthRecord record = new StrengthRecord(id, exercise, oneRepMax, enteredWeight, enteredReps, date);
                        records.add(record);
                    }

                    checkedColumns = true;
                    callback.onSuccess(records);

                }
                catch (JSONException e)
                {
                    callback.onError("Ошибка при парсинге данных: " + e.getMessage());
                }
            }
        });
    }

    public void addStrengthRecord(StrengthRecord record, final SupabaseCallback<Boolean> callback)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("exercise", record.getExercise());
            jsonObject.put("one_rep_max", record.getOneRepMax());
            jsonObject.put("date", record.getDate());

            if (hasEnteredWeightColumn)
            {
                jsonObject.put("entered_weight", record.getEnteredWeight());
            }

            if (hasEnteredRepsColumn)
            {
                jsonObject.put("entered_reps", record.getEnteredReps());
            }

            String jsonBody = jsonObject.toString();
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/strength_records")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String responseBody = response.body().string();

                    if (response.isSuccessful())
                    {
                        callback.onSuccess(true);
                    }
                    else
                    {
                        callback.onError("Ошибка при добавлении силового показателя: " + response.code() + " - " + responseBody);
                    }
                }
            });
        } catch (JSONException e)
        {
            callback.onError(e.getMessage());
        }
    }

    public void deleteStrengthRecord(int id, final SupabaseCallback<Boolean> callback)
    {
        try
        {
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/strength_records?id=eq." + id)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .delete()
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String responseBody = response.body().string();

                    if (response.isSuccessful())
                    {
                        callback.onSuccess(true);
                    }
                    else
                    {
                        callback.onError("Ошибка при удалении силового показателя: " + response.code() + " - " + responseBody);
                    }
                }
            });
        } catch (Exception e)
        {
            callback.onError(e.getMessage());
        }
    }

    public void getProducts(final SupabaseCallback<List<Product>> callback)
    {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/products?select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (!response.isSuccessful())
                {
                    callback.onError("Ошибка при получении продуктов: " + response.code());
                    return;
                }

                try
                {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    List<Product> products = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        double calories = jsonObject.getDouble("calories");
                        double protein = jsonObject.getDouble("protein");
                        double fat = jsonObject.getDouble("fat");
                        double carbs = jsonObject.getDouble("carbs");

                        Product product = new Product(id, name, calories, protein, fat, carbs);
                        products.add(product);
                    }

                    callback.onSuccess(products);

                }
                catch (JSONException e)
                {
                    callback.onError("Ошибка при парсинге данных: " + e.getMessage());
                }
            }
        });
    }

    public void addProduct(Product product, final SupabaseCallback<Boolean> callback)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", product.getName());
            jsonObject.put("calories", product.getCalories());
            jsonObject.put("protein", product.getProtein());
            jsonObject.put("fat", product.getFat());
            jsonObject.put("carbs", product.getCarbs());

            String jsonBody = jsonObject.toString();
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/products")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String responseBody = response.body().string();

                    if (response.isSuccessful())
                    {
                        callback.onSuccess(true);
                    }
                    else
                    {
                        callback.onError("Ошибка при добавлении продукта: " + response.code() + " - " + responseBody);
                    }
                }
            });
        }
        catch (JSONException e)
        {
            callback.onError(e.getMessage());
        }
    }

    public void deleteProduct(int id, final SupabaseCallback<Boolean> callback)
    {
        try
        {
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/products?id=eq." + id)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .delete()
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String responseBody = response.body().string();

                    if (response.isSuccessful())
                    {
                        callback.onSuccess(true);
                    }
                    else
                    {
                        callback.onError("Ошибка при удалении продукта: " + response.code() + " - " + responseBody);
                    }
                }
            });
        }
        catch (Exception e)
        {
            callback.onError(e.getMessage());
        }
    }
}