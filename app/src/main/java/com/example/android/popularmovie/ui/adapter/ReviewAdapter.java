package com.example.android.popularmovie.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.databinding.ReviewRecyclerViewHolderBinding;
import com.example.android.popularmovie.room.entity.Review;

import java.util.List;

public final class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviews;
    private Context context;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(ReviewRecyclerViewHolderBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.binding.reviewLabelTextView.setText(context.getString(R.string.review_label_format, position + 1, review.author));
        holder.binding.reviewContentTextView.setText(review.content);
        holder.binding.reviewUrlTextView.setText(review.url);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ReviewRecyclerViewHolderBinding binding;

        ViewHolder(ReviewRecyclerViewHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}