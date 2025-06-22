using System.ComponentModel.DataAnnotations.Schema;
namespace justaddgelang_web_api.Models
{
    public class LoginRequest
    {
        public string username { get; set; }

        public string password { get; set; }
    }

    public class LoginResponse
    {
        public int? user_id { get; set; }
        public string username { get; set; }
        public string email_id { get; set; }
        public string phone_no { get; set; }
        public string usertype { get; set; }
    }

    public class UpdateProfileRequest
    {
        public string username { get; set; }
        public string email_id { get; set; }
        public string phone_no { get; set; }
    }


    public class EmailOtpRequest
    {
        public string Email { get; set; }
    }

    public class EmailOtpValidationRequest
    {
        public string Email { get; set; }
        public string Otp { get; set; }
    }
    public static class InMemoryOtpStore
    {
        private static Dictionary<string, string> otpStore = new();

        public static void StoreOtp(string phone, string otp) => otpStore[phone] = otp;
        public static bool ValidateOtp(string phone, string otp) => otpStore.ContainsKey(phone) && otpStore[phone] == otp;
        public static void RemoveOtp(string phone) => otpStore.Remove(phone);
    }

    public class ChangePasswordRequest
    {
        public string Email { get; set; }
        public string NewPassword { get; set; }
    }

    public class ProductReqModel
    {
        public int CategoryId { get; set; }
        public string ProductName { get; set; }
        public decimal ProductPrice { get; set; }
        public int ProductStock { get; set; }
    }

    public class CreateCartRequest
    {
        public int userId { get; set; }
    }
    public class AddToCartRequest
    {
        public int user_id { get; set; }
        public int product_id { get; set; }
        public int quantity { get; set; }
    }

    public class UpdateCartRequest
    {
        public int cart_item_id { get; set; }
        public int quantity { get; set; }
    }

}
