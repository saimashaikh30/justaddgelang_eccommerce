using Microsoft.AspNetCore.Mvc;
using justaddgelang_web_api.Models;
using Microsoft.EntityFrameworkCore;

namespace justaddgelang_web_api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ReviewController : ControllerBase
    {
        private readonly dbcontext _context;

        public ReviewController(dbcontext context)
        {
            _context = context;
        }

        // ✅ Submit Review (without image)
        [HttpPost("submit")]
        public async Task<IActionResult> SubmitReview(
            [FromForm] int user_id,
            [FromForm] int product_id,
            [FromForm] string review_text,
            [FromForm] int rating,
            [FromForm] string userName)
        {
            Console.WriteLine(user_id);
            var userExists = await _context.users.AnyAsync(u => u.user_id == user_id);
            var productExists = await _context.products.AnyAsync(p => p.product_id == product_id);

            if (!userExists || !productExists)
            {
                Console.WriteLine(userExists.ToString());
                Console.WriteLine("product" + productExists.ToString());
                return NotFound(new { message = "User or Product not found." });
            }


            var review = new reviews
            {
                user_id = user_id,
                product_id = product_id,
                review_text = review_text,
                rating = rating,
                created_at = DateTime.Now
            };

            _context.reviews.Add(review);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                success = true,
                message = "Review submitted successfully"
            });

        }

        // ✅ Get all reviews of a product
        [HttpGet("product/{productId}")]
        public async Task<IActionResult> GetReviewsWithUserDetails(int productId)
        {
            var reviews = await _context.reviews
                .Where(r => r.product_id == productId)
                .OrderByDescending(r => r.created_at)
                .Select(r => new {
                    r.review_id,
                    r.review_text,
                    r.rating,
                    r.created_at,
                    username = _context.users.FirstOrDefault(u => u.user_id == r.user_id).username,
                    product_name = _context.products.FirstOrDefault(p => p.product_id == r.product_id).product_name
                })
                .ToListAsync();

            if (reviews.Count == 0)
                return NotFound(new { message = "No reviews found for this product." });

            return Ok(reviews);
        }
    }
}
