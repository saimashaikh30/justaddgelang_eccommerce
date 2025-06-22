package com.example.justaddgelang

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/login/register")
    fun registerUser(@Body user: User): Call<UserResponse>

    // Category
    @GET("api/category")
    fun getCategories(): Call<List<Category>>

    @GET("api/category/{id}")
    fun getCategory(@Path("id") id: Int): Call<Category>

    @POST("api/category")
    fun addCategory(@Body category: Category): Call<ApiResponse>

    @PUT("api/category/{id}")
    fun updateCategory(@Path("id") id: Int, @Body category: Category): Call<ApiResponse>

    @DELETE("api/category/{id}")
    fun deleteCategory(@Path("id") categoryId: Int): Call<ApiResponse>

    // Addresses
    @POST("api/useraddress/add")
    fun addUserAddress(@Body address: UserAddress): Call<ApiResponse>

    @GET("api/useraddress/get-addresses/{userId}")
    fun getUserAddresses(@Path("userId") userId: Int): Call<List<UserAddress>>

    @POST("api/useraddress/delete-address/{addressId}")
    fun deleteAddress(@Path("addressId") addressId: Int): Call<ApiResponse>

    // OTP
    @POST("api/login/sendotp")
    fun sendOtp(@Body request: EmailOtpRequest): Call<Map<String, String>>

    @POST("api/login/verifyotp")
    fun verifyOtp(@Body request: EmailOtpValidationRequest): Call<Map<String, Any>>

    @POST("api/login/changepassword")
    fun changePassword(@Body request: ChangePasswordRequest): Call<Map<String, Any>>

    // Profile
    @PUT("api/login/updateprofile/{id}")
    fun updateProfile(@Path("id") userId: Int, @Body request: UpdateProfileRequest): Call<DefaultResponse>

    // Products
    @GET("api/product")
    fun getProducts(): Call<List<Product>>

    @POST("api/product")
    fun addProduct(@Body product: Product): Call<ApiResponse>

    @Multipart
    @POST("api/product/add-with-image")
    fun addProductWithImage(
        @Part productPicture: MultipartBody.Part,
        @Part("categoryId") categoryId: RequestBody,
        @Part("productName") productName: RequestBody,
        @Part("productPrice") productPrice: RequestBody,
        @Part("productStock") productStock: RequestBody
    ): Call<ApiResponse>

    @DELETE("api/product/{id}")
    fun deleteProduct(@Path("id") productId: Int): Call<Void>

    @Multipart
    @POST("api/product/edit-with-image")
    fun editProductWithImage(
        @Part("productId") productId: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part("productName") productName: RequestBody,
        @Part("productPrice") productPrice: RequestBody,
        @Part("productStock") productStock: RequestBody,
        @Part productPicture: MultipartBody.Part?
    ): Call<EditProductResponse>

    @GET("api/product/latest")
    fun getLatestProducts(@Query("userId") userId: Int): Call<List<Product>>

    @GET("api/product/search")
    fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("userId") userId: Int
    ): Call<List<Product>>

    @GET("api/product/by-category/{categoryId}")
    fun getProductsByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("userId") userId: Int
    ): Call<List<Product>>

    // Cart
    @GET("api/cart/user/{userId}")
    fun getCartItems(@Path("userId") userId: Int): Call<List<CartItem>>

    @DELETE("api/cart/items/{cartItemId}")
    fun deleteCartItem(@Path("cartItemId") cartItemId: Int): Call<Void>

    @PUT("api/cart/items/{cartItemId}/update-quantity")
    fun updateCartItemQuantity(@Path("cartItemId") cartItemId: Int, @Body request: CartItemQuantityUpdateRequest): Call<Void>

    @POST("api/cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<ApiResponse>

    // Reviews
    // Reviews

    @GET("api/review/product/{productId}")
    fun getProductReviews(@Path("productId") productId: Int): Call<List<ReviewResponse>>

    // ✅ Submit review with image (multipart)
    @Multipart
    @POST("api/review/submit")
    fun submitReviewWithoutImage(
        @Part("user_id") userId: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part("review_text") reviewText: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("username") username: RequestBody
    ): Call<ApiResponse>

    // Orders
//    @GET("api/orders")
//    fun getAllOrders(): Call<List<OrderResponse>>

    @GET("api/orders/user/{userId}")
    fun getOrdersByUser(@Path("userId") userId: Int): Call<List<Order>>


    @GET("api/orders/{orderId}")
    fun getOrderById(@Path("orderId") orderId: Int): Call<OrderResponse>

    @POST("api/orders")
    fun createOrder(@Body order: OrderCreateRequest): Call<ApiResponse>




    @DELETE("api/orders/{orderId}")
    fun deleteOrder(@Path("orderId") orderId: Int): Call<Void>

    @GET("api/orders/details/{orderId}")
    fun getOrderDetails(@Path("orderId") orderId: Int): Call<OrderDetailResponse>

    @GET("api/orders/all")
    fun getAllOrders(): Call<List<Order>>

    @PUT("api/orders/cancel/{orderId}")
    fun cancelOrder(@Path("orderId") orderId: Int): Call<ApiResponse>

    @PUT("api/orders/status/{orderId}")
    fun updateOrderStatus(@Path("orderId") orderId: Int, @Body status: String): Call<Void>

    @POST("api/wishlist/add")
    fun addToWishlist(@Body request: WishlistRequest): Call<ApiResponse>

    @GET("api/Wishlist/user/{userId}")
    fun getWishlistItems(@Path("userId") userId: Int): Call<List<WishlistItem>>

    @DELETE("api/wishlist/{wishlistId}")
    fun removeFromWishlist(@Path("wishlistId") wishlistId: Int): Call<Void>

}
