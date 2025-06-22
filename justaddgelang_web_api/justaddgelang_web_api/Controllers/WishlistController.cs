using justaddgelang_web_api.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;
using System.Linq;
using System;

namespace justaddgelang_web_api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class WishlistController : ControllerBase
    {
        private readonly dbcontext _context;

        public WishlistController(dbcontext context)
        {
            _context = context;
        }

        // 1. Add product to wishlist
        [HttpPost("add")]
        public async Task<IActionResult> AddToWishlist([FromBody] wishlist request)
        {
            if (request.user_id == 0 || request.product_id == 0)
                return BadRequest("User ID and Product ID are required");

            bool alreadyExists = await _context.wishlists.AnyAsync(w =>
                w.user_id == request.user_id && w.product_id == request.product_id);

            if (alreadyExists)
                return Conflict("Product already in wishlist");

            _context.wishlists.Add(request);
            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Product added to wishlist", request.wishlist_id });

        }

        // 2. Get wishlist for a user
        [HttpGet("user/{userId}")]
        public async Task<IActionResult> GetWishlistByUserId(int userId)
        {
            var baseUrl = $"{Request.Scheme}://{Request.Host}/";

            var wishlistItems = await _context.wishlists
                .Where(w => w.user_id == userId)
                .Join(_context.products,
                      w => w.product_id,
                      p => p.product_id,
                      (w, p) => new
                      {
                          w.wishlist_id,
                          w.user_id,
                          p.product_id,
                          p.product_name,
                          p.product_price,
                          product_picture = baseUrl + p.product_picture,
                          p.product_stock
                      })
                .ToListAsync();

            return Ok(wishlistItems);
        }


        // 3. Delete wishlist item by ID
        [HttpDelete("{wishlistId}")]
        public async Task<IActionResult> DeleteWishlistItem(int wishlistId)
        {
            var item = await _context.wishlists.FindAsync(wishlistId);
            if (item == null)
                return NotFound("Wishlist item not found");

            _context.wishlists.Remove(item);
            await _context.SaveChangesAsync();

            return Ok(new { message = "Wishlist item deleted" });
        }
    }
}
