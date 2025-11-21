package com.example.tastebuddies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class FragmentSearch extends Fragment {
    private EditText editTextSearch;

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

        // Setup search bar
        editTextSearch = view.findViewById(R.id.editTextSearch);
        View searchBar = view.findViewById(R.id.searchBar);
        
        if (editTextSearch != null) {
            // Handle search when user presses Enter/Search button
            editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    performSearch();
                    return true;
                }
                return false;
            });
        }
        
        // Make the entire search bar clickable to focus the EditText
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                if (editTextSearch != null) {
                    editTextSearch.requestFocus();
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
            openCategoryResults(category);
        });
    }

    private void performSearch() {
        if (editTextSearch == null) {
            return;
        }
        
        String searchQuery = editTextSearch.getText().toString().trim();
        if (searchQuery.isEmpty()) {
            return;
        }
        
        // Map search query to category
        String category = mapSearchToCategory(searchQuery);
        openCategoryResults(category);
    }

    private String mapSearchToCategory(String searchQuery) {
        String lowerQuery = searchQuery.toLowerCase();
        
        // Map common search terms to categories
        if (lowerQuery.contains("breakfast") || lowerQuery.contains("morning") || 
            lowerQuery.contains("pancake") || lowerQuery.contains("waffle") ||
            lowerQuery.contains("egg") || lowerQuery.contains("brunch")) {
            return "Breakfast";
        } else if (lowerQuery.contains("lunch") || lowerQuery.contains("midday") ||
                   lowerQuery.contains("sandwich") || lowerQuery.contains("sub")) {
            return "Lunch";
        } else if (lowerQuery.contains("dinner") || lowerQuery.contains("evening") ||
                   lowerQuery.contains("steak") || lowerQuery.contains("restaurant")) {
            return "Dinner";
        } else if (lowerQuery.contains("coffee") || lowerQuery.contains("cafe") ||
                   lowerQuery.contains("espresso") || lowerQuery.contains("latte")) {
            return "Coffee";
        } else if (lowerQuery.contains("bar") || lowerQuery.contains("drink") ||
                   lowerQuery.contains("beer") || lowerQuery.contains("wine") ||
                   lowerQuery.contains("cocktail")) {
            return "Bar";
        } else if (lowerQuery.contains("dessert") || lowerQuery.contains("sweet") ||
                   lowerQuery.contains("ice cream") || lowerQuery.contains("cake") ||
                   lowerQuery.contains("bakery") || lowerQuery.contains("donut")) {
            return "Dessert";
        } else {
            // Default to the search query itself (will be used as category)
            return searchQuery;
        }
    }

    private void openCategoryResults(String category) {
        Intent intent = new Intent(getActivity(), CategoryResultsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
