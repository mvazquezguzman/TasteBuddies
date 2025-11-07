package com.example.tastebuddies;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class FragmentSearch extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;

            int left = v.getPaddingLeft();
            int right = v.getPaddingRight();
            int bottom = v.getPaddingBottom();

            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);
            int top = Math.max(topInset + extraPadding, v.getPaddingTop());
            v.setPadding(left, top, right, bottom);
            return insets;
        });

        View searchBar = view.findViewById(R.id.searchBar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                if (mainActivity != null) {
                    // 
                }
            });
        }

        setupCategoryButton(view, R.id.categoryBreakfast, "Breakfast");
        setupCategoryButton(view, R.id.categoryLunch, "Lunch");
        setupCategoryButton(view, R.id.categoryDinner, "Dinner");
        setupCategoryButton(view, R.id.categoryCoffee, "Coffee");
        setupCategoryButton(view, R.id.categoryBar, "Bar");
        setupCategoryButton(view, R.id.categoryDessert, "Dessert");

        return view;
    }

    private void setupCategoryButton(View parentView, int viewId, String category) {
        View categoryView = parentView.findViewById(viewId);
        if (categoryView == null) {
            return;
        }
        categoryView.setOnClickListener(v -> {
            // 
        });
    }
}
