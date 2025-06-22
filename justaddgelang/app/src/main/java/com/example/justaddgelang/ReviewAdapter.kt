package com.example.justaddgelang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val reviews = mutableListOf<Review>()

    fun submitList(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.review_user_name)
        private val reviewTextView: TextView = itemView.findViewById(R.id.review_text)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.review_rating)


        fun bind(review: Review) {
            userNameTextView.text = review.userName
            reviewTextView.text = review.review_text
            ratingBar.rating = review.rating.toFloat()
        }
    }
}
