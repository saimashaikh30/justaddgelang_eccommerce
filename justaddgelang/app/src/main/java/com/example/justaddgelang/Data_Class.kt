package com.example.justaddgelang

import java.io.Serializable

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: User?
)

data class User(
    val user_id: Int?,
    val username: String?,
    val password: String?,
    val usertype: String?,
    val phone_no: String?,
    val email_id: String?
)


data class RegistrationResponse(
    val success: Boolean,
    val message: String,
    val user: User
)

//data class Category(
//    var category_id: Int?,
//    var category_name: String
//)



data class Order(
    val order_id: Int,
    val order_date: String,
    var order_status: String,
    val total_amount: Double,
    val order_items: List<OrderItem>,
    val status:String,
    val temp_order_id: Int
)

data class OrderItem(
    val product_name: String,
    val quantity: Int,
    val price: Double
)

data class UpdateProfileRequest(
    val username: String,
    val email_id: String,
    val phone_no: String
)


data class DefaultResponse(
    val success: Boolean,
    val message: String
)

data class UserAddress(
    val address_id: Int?,              // 👈 Add this line for address ID
    val user_id: Int,
    val address: String,
    val address_type: String,
    val city: String,
    val pincode: String
)



//data class ApiResponse(
//    val message: String
//)

data class PostOffice(
    val Name: String,
    val Pincode: String,
    val District: String,
    val State: String
)

data class PinCodeResponse(
    val Status: String,
    val PostOffice: List<PostOffice>?
)

data class EmailOtpRequest(val email: String)
data class EmailOtpValidationRequest(val email: String, val otp: String)
data class ChangePasswordRequest(
    val email: String,
    val newPassword: String
)


data class Category(
    val category_id: Int = 0,
    var category_name: String
)


data class ApiResponse(
    val success: Boolean,
    val message: String
)

data class Product(
    val productId: Int,
    val productName: String,
    val productPrice: Double,
    val productStock: Int,
    val categoryId: Int,
    val categoryName: String?,
    val imageUrl: String,
    val isInCart: Boolean
):Serializable


data class EditProductResponse(
    val message: String,
    val imageUrl: String
)


data class CartItem(
    val cart_item_id: Int,
    val product_id: Int,
    val product_name: String,
    val product_picture: String,
    val product_price: Double,
    var quantity: Int,
    val product_stock: Int // <- This represents available stock
)


data class CartItemQuantityUpdateRequest(
    val cartItemId: Int,
    val quantity: Int
)


data class AddToCartRequest(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int
)

data class AddReviewRequest(
    val user_id: Int,
    val product_id: Int,
    val rating: Int,
    val comment: String
)
data class ReviewResponse(
    val review_id: Int,
    val username: String,
    val review_text: String,
    val rating: Int
)

data class Review(
    val user_id: Int,
    val product_id: Int,
    val userName: String,
    val review_text: String,
    val rating: Int
)
data class OrderCreateRequest(
    val userId: Int,
    val addressId: Int,
    val recipientName: String?,
    val recipientNumber: String?,
    val totalAmount: Double,
    val orderItems: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: Int,
    val quantity: Int
)

data class OrderResponse(
    val orderId: Int,
    val totalAmount: Double,
    val orderDate: String,
    val status: String
)


data class OrderDetailResponse(
    val temp_order_id: Int,
    val orderDate: String,
    val status: String,
    val totalAmount: Double,
    val customerName: String,
    val recipientName: String,
    val recipientNumber: String,
    val shippingAddress: ShippingAddress,
    val items: List<OrderDetailItem>
)

data class ShippingAddress(
    val address: String,
    val city: String,
    val pincode: String
)

data class OrderDetailItem(
    val product_id: Int,
    val product_name: String,
    val product_picture: String,
    val product_price: Double,
    val quantity: Int,
    val subtotal: Double
)
data class WishlistRequest(
    val user_id: Int,
    val product_id: Int
)


data class WishlistItem(
    val wishlist_id: Int,
    val user_id: Int,
    val product_id: Int,
    val product_name: String,
    val product_price: Double,
    val product_picture: String,
    val product_stock: Int
)

//
//data class OrderItem(
//    val product_id: Int,
//    val product_name: String,
//    val product_picture: String,
//    val product_price: Double,
//    val quantity: Int,
//    val subtotal: Double
//)


