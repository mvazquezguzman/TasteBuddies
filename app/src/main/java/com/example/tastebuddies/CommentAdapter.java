package com.example.tastebuddies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> comments;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getCommentId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comment, parent, false);
            holder = new ViewHolder();
            holder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            holder.textViewUsername = convertView.findViewById(R.id.textViewUsername);
            holder.textViewComment = convertView.findViewById(R.id.textViewComment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = comments.get(position);

        holder.imageViewProfile.setImageResource(android.R.drawable.sym_def_app_icon);

        holder.textViewUsername.setText(comment.getUsername());
        holder.textViewComment.setText(comment.getCommentText());

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewProfile;
        TextView textViewUsername;
        TextView textViewComment;
    }
}
