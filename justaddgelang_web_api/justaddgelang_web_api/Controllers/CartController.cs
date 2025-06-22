using justaddgelang_web_api.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
using System.Linq;
using System.Threading.Tasks;

namespace justaddgelang_web_api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CartController : ControllerBase
    {
        private readonly dbcontext _context;

        public CartController(dbcontext context)
        {
            _context = context;
        }

        // GET: api/cart/user/5
        [HttpGet("user/{userId}")]
        public async Task<IActionResult> GetCartItems(int userId)
        {
            var cart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == userId);
            if (cart == null)
                return NotFound(new { message = "Cart not found" });

            var baseUrl = $"{Request.Scheme}://{Request.Host}/";  // Base URL for image links

            var items = await _context.cart_items
                .Where(ci => ci.cart_id == cart.cart_id)
                .Join(_context.products,
                      ci => ci.product_id,
                      p => p.product_id,
                      (ci, p) => new
                      {
                          ci.cart_item_id,
                          ci.quantity,
                          p.product_id,
                          p.product_name,
                          p.product_price,
                          p.product_stock, // ✅ Include product stock
                          product_picture = string.IsNullOrEmpty(p.product_picture)
                              ? baseUrl + "images/products/default.png"
                              : baseUrl + p.product_picture
                      })
                .ToListAsync();

            return Ok(items);
        }

        // DELETE: api/cart/items/{cartItemId}
        [HttpDelete("items/{cartItemId}")]
        public async Task<IActionResult> DeleteCartItem(int cartItemId)
        {
            var cartItem = await _context.cart_items.FindAsync(cartItemId);
            if (cartItem == null)
                return NotFound(new { message = "Cart item not found" });

            _context.cart_items.Remove(cartItem);
            await _context.SaveChangesAsync();

            return NoContent(); // 204 - Successfully deleted
        }

        // PUT: api/cart/items/{cartItemId}/update-quantity
        [HttpPut("items/{cartItemId}/update-quantity")]
        public async Task<IActionResult> UpdateCartItemQuantity(int cartItemId, [FromBody] CartItemUpdateRequest request)
        {
            var cartItem = await _context.cart_items.FindAsync(cartItemId);
            if (cartItem == null)
                return NotFound(new { message = "Cart item not found" });

            var product = await _context.products.FindAsync(cartItem.product_id);
            if (product == null)
                return NotFound(new { message = "Product not found" });

            // Validate stock limit
            if (request.Quantity > product.product_stock)
                return BadRequest(new { message = "Quantity exceeds available stock" });

            // Update or remove
            cartItem.quantity = request.Quantity;
            if (cartItem.quantity == 0)
                _context.cart_items.Remove(cartItem);

            await _context.SaveChangesAsync();

            return Ok(new { message = "Cart item updated successfully" });
        }

        [HttpPost("add")]
        public async Task<IActionResult> AddToCart([FromBody] AddToCartRequest request)
        {
            var product = await _context.products.FindAsync(request.product_id);
            if (product == null)
                return NotFound(new { success = false, message = "Product not found" });

            if (request.quantity <= 0 || request.quantity > product.product_stock)
                return BadRequest(new { success = false, message = "Invalid quantity or out of stock" });

            // Only try to find existing cart
            var cart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == request.user_id);
            if (cart == null)
                return BadRequest(new { success = false, message = "Cart not found for user" });

            var existingItem = await _context.cart_items
                .FirstOrDefaultAsync(ci => ci.cart_id == cart.cart_id && ci.product_id == request.product_id);

            if (existingItem != null)
            {
                int newQuantity = existingItem.quantity + request.quantity;
                if (newQuantity > product.product_stock)
                    return BadRequest(new { success = false, message = "Total quantity exceeds stock" });

                existingItem.quantity = newQuantity;
                _context.cart_items.Update(existingItem);
            }
            else
            {
                var cartItem = new cart_items
                {
                    cart_id = cart.cart_id,
                    product_id = request.product_id,
                    quantity = request.quantity
                };
                await _context.cart_items.AddAsync(cartItem);
            }

            await _context.SaveChangesAsync();
            return Ok(new { success = true, message = "Item added to cart" });
        }

        // DELETE: api/cart/user/{userId}
        [HttpDelete("user/{userId}")]
        public async Task<IActionResult> DeleteUserCart(int userId)
        {
            var cart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == userId);
            if (cart == null)
                return NotFound(new { success = false, message = "Cart not found for user" });

            var cartItems = _context.cart_items.Where(ci => ci.cart_id == cart.cart_id);
            _context.cart_items.RemoveRange(cartItems); // Delete all items in the cart

          

            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Cart and items deleted successfully" });
        }

        // DTO class
        public class CartItemUpdateRequest
        {
            public int Quantity { get; set; }
        }
    }
}

