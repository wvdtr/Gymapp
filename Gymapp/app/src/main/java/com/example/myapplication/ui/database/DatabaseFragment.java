package com.example.myapplication.ui.database;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.databinding.FragmentDatabaseBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DatabaseFragment extends Fragment
{

    private FragmentDatabaseBinding binding;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentDatabaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;

        setupViewPager();

        return root;
    }

    private void setupViewPager()
    {
        DatabasePagerAdapter adapter = new DatabasePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
        {
            if (position == 0)
            {
                tab.setText("Продукты");
            }
            else
            {
                tab.setText("Силовые показатели");
            }
        }).attach();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}