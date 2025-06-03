package com.example.myapplication.ui.calc_kbju;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Product;
import com.example.myapplication.R;
import com.example.myapplication.SupabaseClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalckbjuFragment extends Fragment
{
    private Map<String, Product> productMap = new HashMap<>();
    private List<String> productNames = new ArrayList<>();
    private ArrayAdapter<String> productAdapter1;
    private ArrayAdapter<String> productAdapter2;
    private Spinner productSpinner1;
    private Spinner productSpinner2;

    public static CalckbjuFragment newInstance() {
        return new CalckbjuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_calc_kbju, container, false);

        productSpinner1 = v.findViewById(R.id.spinner_calc_kbju_dish_1);
        productSpinner2 = v.findViewById(R.id.spinner_calc_kbju_dish_2);

        productAdapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, productNames);
        productAdapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, productNames);
        
        productSpinner1.setAdapter(productAdapter1);
        productSpinner2.setAdapter(productAdapter2);

        loadProducts();

        TextView k1, k2, b1, b2, j1, j2, u1, u2, ks, bs, js, us;
        k1 = v.findViewById(R.id.calc_kbju_k_1);
        k2 = v.findViewById(R.id.calc_kbju_k_2);
        ks = v.findViewById(R.id.calc_kbju_k_sum);

        b1 = v.findViewById(R.id.calc_kbju_b_1);
        b2 = v.findViewById(R.id.calc_kbju_b_2);
        bs = v.findViewById(R.id.calc_kbju_b_sum);

        j1 = v.findViewById(R.id.calc_kbju_j_1);
        j2 = v.findViewById(R.id.calc_kbju_j_2);
        js = v.findViewById(R.id.calc_kbju_j_sum);

        u1 = v.findViewById(R.id.calc_kbju_u_1);
        u2 = v.findViewById(R.id.calc_kbju_u_2);
        us = v.findViewById(R.id.calc_kbju_u_sum);

        EditText weight1, weight2;
        weight1 = v.findViewById(R.id.edittext_calc_kbju_weight_1);
        weight2 = v.findViewById(R.id.edittext_calc_kbju_weight_2);

        Button b;
        b = v.findViewById(R.id.calc_kbju_button);
        
        Button addProductButton = v.findViewById(R.id.button_add_product);
        addProductButton.setOnClickListener(view -> showAddProductDialog());

        b.setOnClickListener(v2 ->
        {
            String productName1 = productSpinner1.getSelectedItem() != null ? productSpinner1.getSelectedItem().toString() : "";
            String productName2 = productSpinner2.getSelectedItem() != null ? productSpinner2.getSelectedItem().toString() : "";
            
            int w1 = 0, w2 = 0, kd1 = 0, kd2 = 0, bd1 = 0, bd2 = 0, jd1 = 0, jd2 = 0, ud1 = 0, ud2 = 0;
            try
            {
                w1 = Integer.parseInt(weight1.getText().toString());
            }
            catch (NumberFormatException exception)
            {
                w1 = 0;
            }

            try
            {
                w2 = Integer.parseInt(weight2.getText().toString());
            }
            catch (NumberFormatException exception)
            {
                w2 = 0;
            }

            if (!productName1.isEmpty() && productMap.containsKey(productName1))
            {
                Product product = productMap.get(productName1);
                kd1 = (int)(product.getCalories() * w1 / 100);
                bd1 = (int)(product.getProtein() * w1 / 100);
                jd1 = (int)(product.getFat() * w1 / 100);
                ud1 = (int)(product.getCarbs() * w1 / 100);
                
                k1.setText("К: " + kd1);
                b1.setText("Б: " + bd1);
                j1.setText("Ж: " + jd1);
                u1.setText("У: " + ud1);
            }
            else
            {
                k1.setText("К: 0");
                b1.setText("Б: 0");
                j1.setText("Ж: 0");
                u1.setText("У: 0");
            }

            if (!productName2.isEmpty() && productMap.containsKey(productName2))
            {
                Product product = productMap.get(productName2);
                kd2 = (int)(product.getCalories() * w2 / 100);
                bd2 = (int)(product.getProtein() * w2 / 100);
                jd2 = (int)(product.getFat() * w2 / 100);
                ud2 = (int)(product.getCarbs() * w2 / 100);
                
                k2.setText("К: " + kd2);
                b2.setText("Б: " + bd2);
                j2.setText("Ж: " + jd2);
                u2.setText("У: " + ud2);
            }
            else
            {
                k2.setText("К: 0");
                b2.setText("Б: 0");
                j2.setText("Ж: 0");
                u2.setText("У: 0");
            }

            ks.setText("К: " + (kd1 + kd2));
            bs.setText("Б: " + (bd1 + bd2));
            js.setText("Ж: " + (jd1 + jd2));
            us.setText("У: " + (ud1 + ud2));
        });
        
        return v;
    }
    
    private void loadProducts() {
        SupabaseClient.getInstance().getProducts(new SupabaseClient.SupabaseCallback<List<Product>>()
        {
            @Override
            public void onSuccess(List<Product> result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    productMap.clear();
                    productNames.clear();

                    addDefaultProducts();

                    for (Product product : result)
                    {
                        productMap.put(product.getName(), product);
                        productNames.add(product.getName());
                    }

                    productAdapter1.notifyDataSetChanged();
                    productAdapter2.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading products: " + error, Toast.LENGTH_SHORT).show();
                    addDefaultProducts();
                });
            }
        });
    }
    
    private void addDefaultProducts() {
        addDefaultProduct("Картофельное пюре", 0.88, 0.017, 0.028, 0.15);
        addDefaultProduct("Гречка", 3.08, 0.126, 0.033, 0.571);
        addDefaultProduct("Рис", 3.03, 0.075, 0.026, 0.623);
        addDefaultProduct("Макароны", 1.5, 0.04, 0.03, 0.2);
        addDefaultProduct("Картошка вареная", 0.82, 0.02, 0.004, 0.167);
        addDefaultProduct("Котлеты куриные жареные", 2.22, 0.158, 0.123, 0.15);
        addDefaultProduct("Котлеты говяжьи жареные", 2.47, 0.16, 0.17, 0.08);
        addDefaultProduct("Котлеты куриные запеченные", 1.135, 0.18, 0.014, 0.065);
        addDefaultProduct("Котлеты говяжьи запеченные", 1.358, 0.148, 0.076, 0.02);
        addDefaultProduct("Куриная грудка вареная", 1.37, 0.298, 0.018, 0.005);
    }
    
    private void addDefaultProduct(String name, double calories, double protein, double fat, double carbs)
    {
        Product product = new Product(name, calories * 100, protein * 100, fat * 100, carbs * 100);
        productMap.put(name, product);
        productNames.add(name);
    }
    
    private void showAddProductDialog()
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Добавть новый продукт");
        builder.setView(dialogView);
        
        builder.setPositiveButton("Добавить", (dialog, which) ->
        {
            String name = ((EditText) dialogView.findViewById(R.id.productNameInput)).getText().toString();
            String caloriesStr = ((EditText) dialogView.findViewById(R.id.caloriesInput)).getText().toString();
            String proteinStr = ((EditText) dialogView.findViewById(R.id.proteinInput)).getText().toString();
            String fatStr = ((EditText) dialogView.findViewById(R.id.fatInput)).getText().toString();
            String carbsStr = ((EditText) dialogView.findViewById(R.id.carbsInput)).getText().toString();
            
            if (name.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty() || carbsStr.isEmpty())
            {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try
            {
                double calories = Double.parseDouble(caloriesStr);
                double protein = Double.parseDouble(proteinStr);
                double fat = Double.parseDouble(fatStr);
                double carbs = Double.parseDouble(carbsStr);
                
                Product product = new Product(name, calories, protein, fat, carbs);
                addProduct(product);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Неверный числовой формат", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void addProduct(Product product)
    {
        SupabaseClient.getInstance().addProduct(product, new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    Toast.makeText(getContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show();
                    loadProducts();
                });
            }
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
