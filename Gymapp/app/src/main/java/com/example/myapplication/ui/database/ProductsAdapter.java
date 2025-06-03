package com.example.myapplication.ui.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Product;
import com.example.myapplication.R;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>
{
    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener
    {
        void onProductClick(Product product);
    }

    public ProductsAdapter(List<Product> products, OnProductClickListener listener)
    {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position)
    {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView;
        TextView nutritionTextView;

        public ProductViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productNameTextView);
            nutritionTextView = itemView.findViewById(R.id.productNutritionTextView);
        }

        public void bind(Product product, OnProductClickListener listener)
        {
            nameTextView.setText(product.getName());
            nutritionTextView.setText(String.format("К: %.1f, Б: %.1f, Ж: %.1f, У: %.1f", 
                    product.getCalories(), product.getProtein(), product.getFat(), product.getCarbs()));
            
            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}
