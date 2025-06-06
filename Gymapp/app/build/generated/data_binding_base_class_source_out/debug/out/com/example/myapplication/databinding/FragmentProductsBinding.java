// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentProductsBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button addProductButton;

  @NonNull
  public final TextView noProductsText;

  @NonNull
  public final ProgressBar productsProgressBar;

  @NonNull
  public final RecyclerView productsRecyclerView;

  private FragmentProductsBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button addProductButton, @NonNull TextView noProductsText,
      @NonNull ProgressBar productsProgressBar, @NonNull RecyclerView productsRecyclerView) {
    this.rootView = rootView;
    this.addProductButton = addProductButton;
    this.noProductsText = noProductsText;
    this.productsProgressBar = productsProgressBar;
    this.productsRecyclerView = productsRecyclerView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentProductsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentProductsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_products, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentProductsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addProductButton;
      Button addProductButton = ViewBindings.findChildViewById(rootView, id);
      if (addProductButton == null) {
        break missingId;
      }

      id = R.id.noProductsText;
      TextView noProductsText = ViewBindings.findChildViewById(rootView, id);
      if (noProductsText == null) {
        break missingId;
      }

      id = R.id.productsProgressBar;
      ProgressBar productsProgressBar = ViewBindings.findChildViewById(rootView, id);
      if (productsProgressBar == null) {
        break missingId;
      }

      id = R.id.productsRecyclerView;
      RecyclerView productsRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (productsRecyclerView == null) {
        break missingId;
      }

      return new FragmentProductsBinding((ConstraintLayout) rootView, addProductButton,
          noProductsText, productsProgressBar, productsRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
