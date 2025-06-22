using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Collections.Generic;

namespace justaddgelang_web_api.Models
{
    
  
        public class users
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? user_id { get; set; }

            [Column("username", TypeName = "text")]
            public string username { get; set; }

            [Column("password", TypeName = "text")]
            public string password { get; set; }

            [Column("email_id", TypeName = "text")]
            public string? email_id { get; set; }

            [Column("phone_no", TypeName = "text")]
            public string? phone_no { get; set; }

            [Column("usertype", TypeName = "text")]
            public string? usertype { get; set; }

            //public virtual ICollection<user_address> Addresses { get; set; }
            //public virtual ICollection<cart> Carts { get; set; }
            //public virtual ICollection<orders> Orders { get; set; }
            //public virtual ICollection<reviews> Reviews { get; set; }
            //public virtual ICollection<wishlist> Wishlists { get; set; }
        }

        public class cart
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int cart_id { get; set; }

            [Required]
            [ForeignKey("users")]
            public int user_id { get; set; }

            //public virtual users user { get; set; }
            //public virtual ICollection<cart_items> CartItems { get; set; }
        }

        public class cart_items
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int cart_item_id { get; set; }

            [Required]
            [ForeignKey("cart")]
            public int cart_id { get; set; }

            [Required]
            [ForeignKey("product")]
            public int product_id { get; set; }

            [Column("quantity", TypeName = "int")]
            public int quantity { get; set; }

            //public virtual cart cart { get; set; }
            //public virtual product product { get; set; }
        }

        public class category
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int category_id { get; set; }

            [Column("category_name", TypeName = "text")]
            public string category_name { get; set; }

            //public virtual ICollection<product> Products { get; set; }
        }

        public class product
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? product_id { get; set; }

            [Required]
            [ForeignKey("category")]
            public int category_id { get; set; }

            [Column("product_name", TypeName = "text")]
            public string product_name { get; set; }

            [Column("product_picture", TypeName = "text")]
            public string? product_picture { get; set; }

            [Column("product_price", TypeName = "numeric")]
            public decimal product_price { get; set; }

            [Column("product_stock", TypeName = "int")]
            public int product_stock { get; set; }

            //public virtual category category { get; set; }
            //public virtual ICollection<cart_items> CartItems { get; set; }
            //public virtual ICollection<order_items> OrderItems { get; set; }
            //public virtual ICollection<reviews> Reviews { get; set; }
            //public virtual ICollection<wishlist> Wishlists { get; set; }
        }

    public class ProductRequestModel
    {
        public int CategoryId { get; set; }
        public string ProductName { get; set; }
        public decimal ProductPrice { get; set; }
        public int ProductStock { get; set; }
    }

    public class orders
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? order_id { get; set; }

            [Required]
            [ForeignKey("users")]
            public int user_id { get; set; }

        [Required]
        [ForeignKey("user_address")]
        public int address_id { get; set; }

        [Column("total_amount", TypeName = "numeric")]
            public decimal total_amount { get; set; }

            [Column("order_date", TypeName = "timestamp")]
            public DateTime? order_date { get; set; }

            [Column("status", TypeName = "text")]
            public string? status { get; set; }

        [Column("recipient_name", TypeName = "text")]
        public string? recipient_name { get; set; }
        [Column("recipient_number", TypeName = "text")]
        public string? recipient_number { get; set; }

        [Column("temp_order_id", TypeName = "int")]
        public int? temp_order_id { get; set; }
        //public virtual users user { get; set; }

        //public virtual payment Payment { get; set; } // ✅ Ensure navigation property exists
    }

        public class order_items
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? order_item_id { get; set; }

            [Required]
            [ForeignKey("orders")]
            public int order_id { get; set; }

            [Required]
            [ForeignKey("product")]
            public int product_id { get; set; }

            [Column("quantity", TypeName = "int")]
            public int quantity { get; set; }

            //public virtual orders order { get; set; }
            //public virtual product product { get; set; }
        }

        public class payment
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? payment_id { get; set; }

            [Required]
            [ForeignKey("orders")]
            public int order_id { get; set; }

            [Column("amount", TypeName = "numeric")]
            public decimal amount { get; set; }

            [Column("payment_date", TypeName = "timestamp")]
            public DateTime payment_date { get; set; }

            [Column("status", TypeName = "text")]
            public string status { get; set; }

            //public virtual orders order { get; set; } // ✅ Ensure navigation property exists
        }

    public class user_address
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int? address_id { get; set; }

        [Required]
        [ForeignKey("users")]
        public int user_id { get; set; }

        [Required]
        [Column("address", TypeName = "text")]
        public string address { get; set; }

        [Column("address_type", TypeName = "text")]
        public string? address_type { get; set; }

        [Required]
        [Column("city", TypeName = "varchar(100)")]
        public string city { get; set; }

        [Required]
        [Column("pincode", TypeName = "varchar(10)")]
        public string pincode { get; set; }

        // Navigation property if needed
        // public virtual users User { get; set; }
    }


    public class wishlist
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? wishlist_id { get; set; }

            [Required]
            [ForeignKey("users")]
            public int user_id { get; set; }

            [Required]
            [ForeignKey("product")]
            public int product_id { get; set; }

            //public virtual users user { get; set; }
            //public virtual product product { get; set; }
        }

        public class reviews
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int? review_id { get; set; }

            [Required]
            [ForeignKey("users")]
            public int user_id { get; set; }

            [Required]
            [ForeignKey("product")]
            public int product_id { get; set; }

            [Column("created_at", TypeName = "timestamp")]
            public DateTime created_at { get; set; }

            [Column("review_text", TypeName = "text")]
            public string review_text { get; set; }

            [Column("rating", TypeName = "int")]
            public int rating { get; set; }

            //public virtual users user { get; set; }
            //public virtual product product { get; set; }
        }
    }

