using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using justaddgelang_web_api.Models;

namespace justaddgelang_project.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CategoryController : ControllerBase
    {
        private readonly dbcontext _context;

        public CategoryController(dbcontext context)
        {
            _context = context;
        }

        // GET: api/Category
        [HttpGet]
        public async Task<ActionResult<List<category>>> GetCategories()
        {
            return await _context.categories.ToListAsync();
        }

        // GET: api/Category/{id}
        [HttpGet("{id}")]
        public async Task<ActionResult<category>> GetCategory(int id)
        {
            var category = await _context.categories.FindAsync(id);

            if (category == null)
            {
                return NotFound(new { message = "Category not found" });
            }

            return category;
        }

        // POST: api/Category
        [HttpPost]
        public async Task<IActionResult> AddCategory([FromBody] category category)
        {
            category.category_id = 0; // Let EF generate it
            _context.categories.Add(category);

            try
            {
                await _context.SaveChangesAsync();
                return Ok(new { success = true, message = "Category added." });
            }
            catch (DbUpdateException ex)
            {
                return BadRequest(new { success = false, message = "Category already exists or invalid data.", error = ex.Message });
            }
        }

        // PUT: api/Category/{id}
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateCategory(int id, category updatedCategory)
        {
            if (id != updatedCategory.category_id)
                return BadRequest(new { message = "Category ID mismatch" });

            var existingCategory = await _context.categories.FindAsync(id);
            if (existingCategory == null)
                return NotFound(new { message = "Category not found" });

            existingCategory.category_name = updatedCategory.category_name;

            _context.Entry(existingCategory).State = EntityState.Modified;
            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Category updated successfully" });
        }

        // DELETE: api/Category/{id}
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCategory(int id)
        {
            var category = await _context.categories.FindAsync(id);
            if (category == null)
                return NotFound(new { success = false, message = "Category not found" });

            // Get all products that belong to this category
            var products = _context.products.Where(p => p.category_id == id).ToList();

            // Remove the products first
            _context.products.RemoveRange(products);

            // Now remove the category
            _context.categories.Remove(category);

            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Category and related products deleted" });
        }

    }
}
