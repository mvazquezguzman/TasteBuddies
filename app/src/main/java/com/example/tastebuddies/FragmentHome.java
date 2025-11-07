package com.example.tastebuddies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    private ListView listViewFeed;
    private PostAdapter postAdapter;
    private int currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        listViewFeed = view.findViewById(R.id.listViewFeed);
        loadFeed();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFeed();
    }

    private void loadFeed() {
        List<Post> posts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), posts, new PostAdapter.OnPostInteractionListener() {
            @Override
            public void onPostClick(int postId) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }

            @Override
            public void onLikeClick(int postId) {
                // TODO: Implement like functionality
            }

            @Override
            public void onBookmarkClick(int postId) {
                // TODO: Implement bookmark functionality
            }

            @Override
            public void onCommentClick(int postId) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }

            @Override
            public void onProfileClick(int userId) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        listViewFeed.setAdapter(postAdapter);
    }
}
