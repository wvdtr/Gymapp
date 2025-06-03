package com.example.myapplication.ui.database;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DatabasePagerAdapter extends FragmentStateAdapter
{
    public DatabasePagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        if (position == 0)
        {
            return new ProductsFragment();
        }
        else
        {
            return new StrengthRecordsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
