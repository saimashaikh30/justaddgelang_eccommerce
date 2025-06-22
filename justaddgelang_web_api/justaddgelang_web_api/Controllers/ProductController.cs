using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
using justaddgelang_web_api.Models;
using System;
using System.IO;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;

[ApiController]
[Route("api/[controller]")]
public class ProductController : ControllerBase
{
    private readonly dbcontext _context;
    private readonly IWebHostEnvironment _environment;

    public ProductController(dbcontext context, IWebHostEnvironment environment)
    {
        _context = context;
        _environment = environment;
    }

    // POST: Add product with image
    [HttpPost("add-with-image")]
    public async Task<IActionResult> AddProductWithImage([FromForm] IFormFile productPicture,
                                                          [FromForm] int categoryId,
                                                          [FromForm] string productName,
                                                          [FromForm] decimal productPrice,
                                                          [FromForm] int productStock)
    {
        if (productPicture == null || productPicture.Length == 0)
            return BadRequest(new { message = "Please upload a product image." });

        string wwwRootPath = _environment.WebRootPath ?? Path.Combine(Directory.GetCurrentDirectory(), "wwwroot");
        string folderPath = Path.Combine(wwwRootPath, "images", "products");

        if (!Directory.Exists(folderPath))
            Directory.CreateDirectory(folderPath);

        string fileName = Guid.NewGuid().ToString() + Path.GetExtension(productPicture.FileName);
        string filePath = Path.Combine(folderPath, fileName);

        using (var stream = new FileStream(filePath, FileMode.Create))
        {
            await productPicture.CopyToAsync(stream);
        }

        var product = new product
        {
            category_id = categoryId,
            product_name = productName,
            product_price = productPrice,
            product_stock = productStock,
            product_picture = $"images/products/{fileName}"
        };

        _context.products.Add(product);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Product added successfully", product.product_id });
    }

    // POST: Edit product with optional new image
    [HttpPost("edit-with-image")]
    public async Task<IActionResult> EditProductWithImage([FromForm] int productId,
                                                          [FromForm] int categoryId,
                                                          [FromForm] string productName,
                                                          [FromForm] decimal productPrice,
                                                          [FromForm] int productStock,
                                                          [FromForm] IFormFile? productPicture)
    {
        var product = await _context.products.FindAsync(productId);
        if (product == null)
            return NotFound(new { message = "Product not found" });

        product.product_name = productName;
        product.product_price = productPrice;
        product.product_stock = productStock;
        product.category_id = categoryId;

        if (productPicture != null && productPicture.Length > 0)
        {
            var ext = Path.GetExtension(productPicture.FileName).ToLower();
            var allowedExtensions = new[] { ".jpg", ".jpeg", ".png" };
            if (!allowedExtensions.Contains(ext))
                return BadRequest(new { message = "Invalid image format. Only JPG/PNG allowed." });

            if (!string.IsNullOrEmpty(product.product_picture))
            {
                var oldPath = Path.Combine(_environment.WebRootPath, product.product_picture.Replace('/', Path.DirectorySeparatorChar));
                if (System.IO.File.Exists(oldPath)) System.IO.File.Delete(oldPath);
            }

            var folderPath = Path.Combine(_environment.WebRootPath, "images", "products");
            if (!Directory.Exists(folderPath))
                Directory.CreateDirectory(folderPath);

            var fileName = Guid.NewGuid().ToString() + ext;
            var filePath = Path.Combine(folderPath, fileName);
            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await productPicture.CopyToAsync(stream);
            }

            product.product_picture = $"images/products/{fileName}";
        }

        await _context.SaveChangesAsync();
        return Ok(new
        {
            message = "Product updated successfully",
            imageUrl = $"{Request.Scheme}://{Request.Host}/{product.product_picture}"
        });
    }

    // DELETE: Delete product by id
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteProduct(int id)
    {
        var product = await _context.products.FindAsync(id);
        if (product == null)
            return NotFound();

        var imagePath = Path.Combine(_environment.WebRootPath, product.product_picture.Replace('/', Path.DirectorySeparatorChar));
        if (System.IO.File.Exists(imagePath))
            System.IO.File.Delete(imagePath);

        _context.products.Remove(product);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // GET: All products
    [HttpGet]
    public async Task<ActionResult<IEnumerable<object>>> GetProducts()
    {
        var baseUrl = $"{Request.Scheme}://{Request.Host}/";

        var result = await (from p in _context.products
                            join c in _context.categories on p.category_id equals c.category_id into pc
                            from category in pc.DefaultIfEmpty()
                            select new
                            {
                                productId = p.product_id,
                                productName = p.product_name,
                                productPrice = p.product_price,
                                productStock = p.product_stock,
                                categoryId = p.category_id,
                                categoryName = category != null ? category.category_name : "Unknown",
                                imageUrl = string.IsNullOrEmpty(p.product_picture)
                                    ? baseUrl + "images/products/default.png"
                                    : baseUrl + p.product_picture
                            }).ToListAsync();

        return Ok(result);
    }

    // GET: Latest products (limit 10)
    [HttpGet("latest")]
    public async Task<ActionResult<IEnumerable<object>>> GetLatestProducts([FromQuery] int userId)
    {
        var baseUrl = $"{Request.Scheme}://{Request.Host}/";

        var userCart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == userId);
        var cartProductIds = userCart != null
            ? await _context.cart_items.Where(ci => ci.cart_id == userCart.cart_id).Select(ci => ci.product_id).ToListAsync()
            : new List<int>();

        var products = await (from p in _context.products
                              join c in _context.categories on p.category_id equals c.category_id into pc
                              from category in pc.DefaultIfEmpty()
                              orderby p.product_id descending
                              select new
                              {
                                  productId = p.product_id,
                                  productName = p.product_name,
                                  productPrice = p.product_price,
                                  productStock = p.product_stock,
                                  categoryId = p.category_id,
                                  categoryName = category != null ? category.category_name : "Unknown",
                                  imageUrl = string.IsNullOrEmpty(p.product_picture)
                                      ? baseUrl + "images/products/default.png"
                                      : baseUrl + p.product_picture,
                                  isInCart = cartProductIds.Contains((int)p.product_id)
                              })
                             .Take(10)
                             .ToListAsync();

        return Ok(products);
    }

    // GET: Search products with pagination
    [HttpGet("search")]
    public async Task<ActionResult<IEnumerable<object>>> SearchProducts(string query, int page = 1, int pageSize = 10, [FromQuery] int userId = 0)
    {
        var baseUrl = $"{Request.Scheme}://{Request.Host}/";
        var skip = (page - 1) * pageSize;

        var userCart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == userId);
        var cartProductIds = userCart != null
            ? await _context.cart_items.Where(ci => ci.cart_id == userCart.cart_id).Select(ci => ci.product_id).ToListAsync()
            : new List<int>();

        var products = await _context.products
            .Where(p => p.product_name.Contains(query))
            .OrderByDescending(p => p.product_id)
            .Skip(skip)
            .Take(pageSize)
            .Select(p => new
            {
                productId = p.product_id,
                productName = p.product_name,
                productPrice = p.product_price,
                productStock = p.product_stock,
                categoryId = p.category_id,
                imageUrl = baseUrl + p.product_picture,
                isInCart = cartProductIds.Contains((int)p.product_id)
            }).ToListAsync();

        return Ok(products);
    }

    // GET: Products by category
    [HttpGet("by-category/{categoryId}")]
    public async Task<ActionResult<IEnumerable<object>>> GetProductsByCategory(int categoryId, [FromQuery] int userId = 0)
    {
        var baseUrl = $"{Request.Scheme}://{Request.Host}/";

        var userCart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == userId);
        var cartProductIds = userCart != null
            ? await _context.cart_items.Where(ci => ci.cart_id == userCart.cart_id).Select(ci => ci.product_id).ToListAsync()
            : new List<int>();

        var products = await (from p in _context.products
                              join c in _context.categories on p.category_id equals c.category_id into pc
                              from category in pc.DefaultIfEmpty()
                              where p.category_id == categoryId
                              orderby p.product_id descending
                              select new
                              {
                                  productId = p.product_id,
                                  productName = p.product_name,
                                  productPrice = p.product_price,
                                  productStock = p.product_stock,
                                  categoryId = p.category_id,
                                  categoryName = category != null ? category.category_name : "Unknown",
                                  imageUrl = string.IsNullOrEmpty(p.product_picture)
                                      ? baseUrl + "images/products/default.png"
                                      : baseUrl + p.product_picture,
                                  isInCart = cartProductIds.Contains((int)p.product_id)
                              }).ToListAsync();

        return Ok(products);
    }


}
