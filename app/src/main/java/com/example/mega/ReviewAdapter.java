package com.example.mega;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final List<Review> reviews;
    private LayoutInflater inflater;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.reviews = reviews;
        this.inflater = LayoutInflater.from(context);
    }

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.userName.setText(review.getUserName());
        holder.rating.setRating(review.getRating());
        holder.reviewText.setText(review.getText());
        holder.reviewDate.setText(review.getDate());
    }
    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final RatingBar ratingBar;
        private final TextView reviewText;
        private final TextView reviewDate;
        public RatingBar rating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.review_user_name);
            ratingBar = itemView.findViewById(R.id.review_rating);
            reviewText = itemView.findViewById(R.id.review_text);
            reviewDate = itemView.findViewById(R.id.review_date);
        }

        public void bind(Review review) {
            userName.setText(review.getUserName());
            ratingBar.setRating(review.getRating());
            reviewText.setText(review.getText());
            reviewDate.setText(review.getDate());
        }
    }
}
