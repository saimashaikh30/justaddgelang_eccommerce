package com.example.justaddgelang

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var addToCartButton: ImageButton
    private lateinit var addToWishlistButton: ImageButton
    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var reviewRatingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private lateinit var submitReviewButton: Button

    private lateinit var reviewAdapter: ReviewAdapter
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_detail)

        productImage = findViewById(R.id.product_image)
        productName = findViewById(R.id.product_name)
        productPrice = findViewById(R.id.product_price)
        addToCartButton = findViewById(R.id.add_to_cart_button)
        addToWishlistButton = findViewById(R.id.add_to_wishlist_button)
        reviewRecyclerView = findViewById(R.id.review_recycle_view)
        reviewRatingBar = findViewById(R.id.review_rating_bar)
        reviewEditText = findViewById(R.id.review_text_input)
        submitReviewButton = findViewById(R.id.submit_review_button)

        product = intent.getSerializableExtra("product") as? Product
        if (product == null) {
            Toast.makeText(this, "No product data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        reviewAdapter = ReviewAdapter()
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        reviewRecyclerView.adapter = reviewAdapter

        loadProductDetails()
        loadReviews()

        addToWishlistButton.setOnClickListener {
            val userIdString = SharedPreferencesHelper.getUserId(this)
            val userId = userIdString?.toIntOrNull() ?: -1
            val productId = product?.productId ?: return@setOnClickListener

            val wishlistRequest = WishlistRequest(user_id = userId, product_id = productId)

            RetrofitClient.apiService.addToWishlist(wishlistRequest)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@ProductDetailActivity, "Added to wishlist!", Toast.LENGTH_SHORT).show()
                        } else if (response.code() == 409) {
                            Toast.makeText(this@ProductDetailActivity, "Product already in wishlist", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ProductDetailActivity, "Failed to add to wishlist", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@ProductDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        addToCartButton.setOnClickListener {
            val userIdString = SharedPreferencesHelper.getUserId(this)
            val userId = userIdString?.toIntOrNull() ?: -1
            val productId = product?.productId ?: return@setOnClickListener
            val quantity = 1

            val request = AddToCartRequest(user_id = userId, product_id = productId, quantity = quantity)

            RetrofitClient.apiService.addToCart(request).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ProductDetailActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@ProductDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        submitReviewButton.setOnClickListener {
            submitReview()
        }
    }

    private fun loadProductDetails() {
        product?.let {
            productName.text = it.productName
            productPrice.text = "₹${it.productPrice}"
            Glide.with(this).load(it.imageUrl).into(productImage)
        }
    }

    private fun loadReviews() {
        product?.let {
            RetrofitClient.apiService.getProductReviews(it.productId)
                .enqueue(object : Callback<List<ReviewResponse>> {
                    override fun onResponse(
                        call: Call<List<ReviewResponse>>,
                        response: Response<List<ReviewResponse>>
                    ) {
                        if (response.isSuccessful) {
                            val reviewResponses = response.body() ?: emptyList()
                            if (reviewResponses.isNotEmpty()) {
                                val reviews = reviewResponses.map { res ->
                                    Review(
                                        user_id = res.review_id,
                                        product_id = product!!.productId,
                                        userName = res.username,
                                        review_text = res.review_text,
                                        rating = res.rating,
                                    )
                                }
                                reviewAdapter.submitList(reviews)
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<ReviewResponse>>, t: Throwable) {
                        // Optional: Log error
                    }
                })
        }
    }

    private fun submitReview() {
        val rating = reviewRatingBar.rating.toInt()
        val reviewText = reviewEditText.text.toString().trim()
        val username = "UserName" // Replace with actual logged-in user's name

        if (reviewText.isEmpty()) {
            reviewEditText.error = "Please write a review"
            return
        }

        val productId = product?.productId ?: return
        val userIdString = SharedPreferencesHelper.getUserId(this)
        val userId = userIdString?.toIntOrNull() ?: -1

        val userIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        val productIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), productId.toString())
        val reviewTextPart = RequestBody.create("text/plain".toMediaTypeOrNull(), reviewText)
        val ratingPart = RequestBody.create("text/plain".toMediaTypeOrNull(), rating.toString())
        val usernamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), username)

        RetrofitClient.apiService.submitReviewWithoutImage(
            userIdPart, productIdPart, reviewTextPart, ratingPart, usernamePart
        ).enqueue(reviewCallback)
    }

    private val reviewCallback = object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                Toast.makeText(this@ProductDetailActivity, "Review submitted!", Toast.LENGTH_SHORT).show()
                reviewEditText.setText("")
                reviewRatingBar.rating = 0f
                loadReviews()
            } else {
                Toast.makeText(this@ProductDetailActivity, "Failed to submit review", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            Toast.makeText(this@ProductDetailActivity, "Error submitting review", Toast.LENGTH_SHORT).show()
        }
    }
}
