using justaddgelang_web_api.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Linq;
using System.Net;
using System.Threading.Tasks;

namespace justaddgelang_web_api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UserAddressController : ControllerBase
    {
        private readonly dbcontext _context;

        public UserAddressController(dbcontext context)
        {
            _context = context;
        }
        [HttpPost("add")]
        public async Task<IActionResult> AddAddress([FromBody] user_address address)
        {
            if (address == null)
                return BadRequest("Address is null.");

            // Check for duplicate address (case-insensitive comparison)
            bool isDuplicate = await _context.user_addresses
                .AnyAsync(a =>
                    a.user_id == address.user_id &&
                    a.address.ToLower() == address.address.ToLower() &&
                    a.city.ToLower() == address.city.ToLower() &&
                    a.pincode == address.pincode
                );

            if (isDuplicate)
                return Conflict(new { message = "This address already exists for the user." });

            _context.Add(address);
            await _context.SaveChangesAsync();

            return Ok(new { message = "Address saved successfully" });
        }


        [HttpGet("get-addresses/{userId}")]
        public async Task<IActionResult> GetUserAddresses(int userId)
        {
            var addresses = await _context.user_addresses
                .Where(a => a.user_id == userId)
                .ToListAsync();
            return Ok(addresses);
        }

        [HttpPost("delete-address/{addressId}")]
        public async Task<IActionResult> DeleteAddress(int addressId)
        {
            var address = await _context.user_addresses.FindAsync(addressId);
            if (address == null) return NotFound();

            _context.user_addresses.Remove(address);
            await _context.SaveChangesAsync();
            return Ok(new { message = "Address deleted" });
        }

    }
}
