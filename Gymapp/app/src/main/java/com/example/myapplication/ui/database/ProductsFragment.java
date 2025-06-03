package com.example.myapplication.ui.database;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Product;
import com.example.myapplication.R;
import com.example.myapplication.SupabaseClient;
import com.example.myapplication.databinding.FragmentProductsBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener
{

    private FragmentProductsBinding binding;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private SupabaseClient supabaseClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        supabaseClient = SupabaseClient.getInstance();
        recyclerView = binding.productsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductsAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        binding.addProductButton.setOnClickListener(v -> showAddProductDialog());

        loadProducts();

        return root;
    }

    private void loadProducts()
    {
        binding.productsProgressBar.setVisibility(View.VISIBLE);
        supabaseClient.getProducts(new SupabaseClient.SupabaseCallback<List<Product>>()
        {
            @Override
            public void onSuccess(List<Product> result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    productList.clear();
                    productList.addAll(result);
                    adapter.notifyDataSetChanged();
                    
                    if (productList.isEmpty())
                    {
                        binding.noProductsText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        binding.noProductsText.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка загрузки продуктов: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void showAddProductDialog()
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Добавить новый продукт");
        builder.setView(dialogView);
        
        builder.setPositiveButton("Добавить", (dialog, which) ->
        {
            String name = ((android.widget.EditText) dialogView.findViewById(R.id.productNameInput)).getText().toString();
            String caloriesStr = ((android.widget.EditText) dialogView.findViewById(R.id.caloriesInput)).getText().toString();
            String proteinStr = ((android.widget.EditText) dialogView.findViewById(R.id.proteinInput)).getText().toString();
            String fatStr = ((android.widget.EditText) dialogView.findViewById(R.id.fatInput)).getText().toString();
            String carbsStr = ((android.widget.EditText) dialogView.findViewById(R.id.carbsInput)).getText().toString();
            
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
            }
            catch (NumberFormatException e)
            {
                Toast.makeText(getContext(), "Неверный числовой формат", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addProduct(Product product)
    {
        binding.productsProgressBar.setVisibility(View.VISIBLE);
        supabaseClient.addProduct(product, new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show();
                    loadProducts();
                });
            }

            @Override
            public void onError(String error)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onProductClick(Product product)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Удаление продукта")
                .setMessage("Вы уверены, что хотите удалить " + product.getName() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> deleteProduct(product.getId()))
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteProduct(int productId)
    {
        binding.productsProgressBar.setVisibility(View.VISIBLE);
        supabaseClient.deleteProduct(productId, new SupabaseClient.SupabaseCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean result)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Продукт успешно удален", Toast.LENGTH_SHORT).show();
                    loadProducts();
                });
            }

            @Override
            public void onError(String error)
            {
                requireActivity().runOnUiThread(() ->
                {
                    binding.productsProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка удаления продукта: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
