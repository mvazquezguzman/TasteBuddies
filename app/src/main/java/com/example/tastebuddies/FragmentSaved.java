package com.example.tastebuddies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentSaved extends Fragment {
    private ListView listViewSaved;
    private PostAdapter adapter;
    private int currentUserId;
    private TextView textViewEmpty;
    private TextView textViewWantToTryCount;
    private LinearLayout layoutWantToTry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUserId = mainActivity.getCurrentUserId();
        }

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);
            int top = Math.max(topInset + extraPadding, v.getPaddingTop());
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        listViewSaved = view.findViewById(R.id.listViewSaved);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewWantToTryCount = view.findViewById(R.id.textViewWantToTryCount);
        layoutWantToTry = view.findViewById(R.id.layoutWantToTry);

        loadCounts();
        loadSavedPosts();

        layoutWantToTry.setOnClickListener(v -> {
            // Already in Want to Try section, just refresh
            loadSavedPosts();
        });

        listViewSaved.setOnItemClickListener((parent, view1, position, id) -> {
            if (adapter != null && adapter.getCount() > position) {
                Post post = (Post) adapter.getItem(position);
                if (post != null) {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCounts();
        loadSavedPosts();
    }

    private void loadCounts() {
        textViewWantToTryCount.setText("0");
    }

    private void loadSavedPosts() {
        List<Post> savedPosts = new ArrayList<>();
        
        textViewEmpty.setVisibility(View.VISIBLE);
        listViewSaved.setVisibility(View.GONE);
    }
}

