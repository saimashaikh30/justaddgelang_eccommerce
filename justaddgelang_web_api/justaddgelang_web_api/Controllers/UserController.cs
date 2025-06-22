using justaddgelang_web_api.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using BCrypt.Net;
using System;
using System.Threading.Tasks;

namespace justaddgelang_web_api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LoginController : ControllerBase
    {
        private readonly dbcontext _context;
        private readonly IEmailService _emailService;

        public LoginController(dbcontext context, IEmailService emailService)
        {
            _context = context;
            _emailService = emailService;
        }

        // POST: api/login
        [HttpPost]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.username) || string.IsNullOrWhiteSpace(request.password))
                return BadRequest(new { success = false, message = "Username and password are required." });

            var user = await _context.users.FirstOrDefaultAsync(u => u.username == request.username);
            if (user == null)
                return NotFound(new { success = false, message = "User not found." });

            bool isPasswordValid = BCrypt.Net.BCrypt.Verify(request.password, user.password);
            if (!isPasswordValid)
                return Unauthorized(new { success = false, message = "Invalid username or password." });

            var responseUser = new LoginResponse
            {
                user_id = user.user_id,
                username = user.username,
                email_id = user.email_id,
                phone_no = user.phone_no,
                usertype = user.usertype
            };

            string subject = "🛡️ New Login to Your justaddgelang Account";
            string body = $@"Hi {user.username},👋 
We noticed a new login to your justaddgelang account:
🕒 Time: {DateTime.Now:f}
If this was you, you’re good to go!
If you didn’t initiate this login, we strongly recommend changing your password immediately.
Stay secure, 
-charms by Saima💫";

            await _emailService.SendEmailAsync(user.email_id, subject, body);

            return Ok(new { success = true, message = "Login successful.", user = responseUser });
        }

        // POST: api/login/register
        [HttpPost("register")]
        public async Task<IActionResult> CreateUser([FromBody] users user)
        {
            if (user == null)
                return BadRequest(new { success = false, message = "Invalid user data." });

            if (await _context.users.AnyAsync(u => u.username == user.username))
                return Conflict(new { success = false, message = "Username already exists." });

            if (await _context.users.AnyAsync(u => u.email_id == user.email_id))
                return Conflict(new { success = false, message = "Email is already registered." });

            if (await _context.users.AnyAsync(u => u.phone_no == user.phone_no))
                return Conflict(new { success = false, message = "Phone number is already registered." });

            user.password = BCrypt.Net.BCrypt.HashPassword(user.password);
            user.user_id = null; // Let DB auto-generate
            user.usertype ??= "user";

            await _context.users.AddAsync(user);
            await _context.SaveChangesAsync(); // user.user_id will now be populated

            if (!user.user_id.HasValue)
                return StatusCode(500, new { success = false, message = "Failed to generate user ID." });

            var cart = new cart
            {
                user_id = user.user_id.Value
            };

            await _context.carts.AddAsync(cart);
            await _context.SaveChangesAsync();

            await _emailService.SendEmailAsync(user.email_id, "Welcome to justAddGelang!", $"Hello {user.username},\n\nYour account has been successfully created. Welcome aboard!\n\n— charms by Saima💫");

            var responseUser = new LoginResponse
            {
                user_id = user.user_id,
                username = user.username,
                email_id = user.email_id,
                phone_no = user.phone_no,
                usertype = user.usertype
            };

            return Ok(new { success = true, message = "User registered successfully.", user = responseUser });
        }

        // PUT: api/login/updateprofile/{id}
        [HttpPut("updateprofile/{id}")]
        public async Task<IActionResult> UpdateProfile(int id, [FromBody] UpdateProfileRequest updatedUser)
        {
            var user = await _context.users.FindAsync(id);
            if (user == null)
                return NotFound(new { success = false, message = "User not found." });

            user.username = updatedUser.username;
            user.email_id = updatedUser.email_id;
            user.phone_no = updatedUser.phone_no;

            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Profile updated successfully." });
        }

        // POST: api/login/sendotp
        [HttpPost("sendotp")]
        public async Task<IActionResult> SendOtp([FromBody] EmailOtpRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Email))
                return BadRequest(new { success = false, message = "Email is required" });

            var user = await _context.users.FirstOrDefaultAsync(u => u.email_id == request.Email);
            if (user == null)
                return NotFound(new { success = false, message = "Email not registered" });

            string otp = new Random().Next(100000, 999999).ToString();
            InMemoryOtpStore.StoreOtp(request.Email, otp);

            string subject = "Your justAddGelang OTP";
            string body = $"Hi {user.username},\n\nYour OTP is: {otp}\n\n— charms by Saima💫";

            await _emailService.SendEmailAsync(request.Email, subject, body);

            return Ok(new { success = true, message = "OTP sent to email" });
        }

        // POST: api/login/verifyotp
        [HttpPost("verifyotp")]
        public IActionResult VerifyOtp([FromBody] EmailOtpValidationRequest request)
        {
            if (InMemoryOtpStore.ValidateOtp(request.Email, request.Otp))
            {
                InMemoryOtpStore.RemoveOtp(request.Email);
                return Ok(new { success = true, message = "OTP verified" });
            }

            return BadRequest(new { success = false, message = "Invalid or expired OTP" });
        }

        // POST: api/login/changepassword
        [HttpPost("changepassword")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.NewPassword))
                return BadRequest(new { success = false, message = "Email and new password are required." });

            var user = await _context.users.FirstOrDefaultAsync(u => u.email_id == request.Email);
            if (user == null)
                return NotFound(new { success = false, message = "User not found." });

            // If the old and new password are the same, return an error
            if (BCrypt.Net.BCrypt.Verify(request.NewPassword, user.password))
            {
                return BadRequest(new { success = false, message = "New password cannot be the same as the old password." });
            }

            // Hash and update the new password
            user.password = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Password changed successfully." });
        }

    }
}
