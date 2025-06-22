using System.Collections.Generic;
using justaddgelang_web_api.Models;
using Microsoft.EntityFrameworkCore;

namespace justaddgelang_web_api.Models
{
    public class dbcontext : DbContext
    {
        public dbcontext(DbContextOptions options) : base(options)
        {
        }

        public DbSet<users> users { get; set; }
        public DbSet<cart> carts { get; set; }
        public DbSet<cart_items> cart_items { get; set; }
        public DbSet<category> categories { get; set; }
        public DbSet<product> products { get; set; }
        public DbSet<orders> orders { get; set; }
        public DbSet<order_items> order_items { get; set; }
        public DbSet<payment> payments { get; set; }
        public DbSet<user_address> user_addresses { get; set; }
        public DbSet<wishlist> wishlists { get; set; }
        public DbSet<reviews> reviews { get; set; }
    }
}
