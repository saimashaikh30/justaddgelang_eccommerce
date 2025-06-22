// OrdersController.cs
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using justaddgelang_web_api.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace justaddgelang_web_api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class OrdersController : ControllerBase
    {
        private readonly dbcontext _context;
        private readonly IEmailService _emailService;

        public OrdersController(dbcontext context, IEmailService emailService)
        {
            _context = context;
            _emailService = emailService;
        }

        [HttpPost]
        public async Task<ActionResult<orders>> CreateOrder(OrderCreateRequest request)
        {
            var order = new orders
            {
                user_id = request.UserId,
                address_id = request.AddressId,
                total_amount = request.TotalAmount,
                order_date = DateTime.Now,
                status = "Pending",
                temp_order_id = new Random().Next(1000, 9999),
                recipient_name = request.RecipientName,
                recipient_number = request.RecipientNumber
            };

            _context.orders.Add(order);
            await _context.SaveChangesAsync();

            foreach (var item in request.OrderItems)
            {
                var orderItem = new order_items
                {
                    order_id = order.order_id.Value,
                    product_id = item.ProductId,
                    quantity = item.Quantity
                };
                _context.order_items.Add(orderItem);

                var product = await _context.products.FindAsync(item.ProductId);
                if (product != null)
                {
                    if (product.product_stock < item.Quantity)
                    {
                        return BadRequest(new
                        {
                            success = false,
                            message = $"Not enough stock for product ID {item.ProductId}"
                        });
                    }

                    product.product_stock -= item.Quantity;
                    _context.products.Update(product);
                }
            }

            await _context.SaveChangesAsync();

            // Delete user's cart
            var cart = await _context.carts.FirstOrDefaultAsync(c => c.user_id == request.UserId);
            if (cart != null)
            {
                var cartItems = _context.cart_items.Where(ci => ci.cart_id == cart.cart_id);
                _context.cart_items.RemoveRange(cartItems);
                await _context.SaveChangesAsync();
            }

            // Fetch user info
            var user = await _context.users.FirstOrDefaultAsync(u => u.user_id == request.UserId);
            if (user != null)
            {
                string subject = "📦 Order Placed - justAddGelang";
                string body = $@"Hi {user.username}, 👋

Thanks for your order! 🙌

🧾 Order ID: {order.temp_order_id}
📅 Date: {order.order_date:f}
💰 Total: ₹{order.total_amount}

We’ll notify you once your order is confirmed. If you have any questions, just reply to this email.

— charms by Saima 💫";

                await _emailService.SendEmailAsync(user.email_id, subject, body);
            }

            return Ok(new
            {
                success = true,
                message = "Order placed successfully"
            });
        }




        [HttpGet("user/{userId}")]
        public async Task<IActionResult> GetOrdersByUser(int userId)
        {
            var orders = await _context.orders
                .Where(o => o.user_id == userId)
                .OrderByDescending(o => o.order_date)
                .ToListAsync();

            if (orders == null || !orders.Any())
            {
                return NotFound(new { message = "No orders found for this user." });
            }

            return Ok(orders);
        }

        [HttpGet("details/{orderId}")]
        public async Task<IActionResult> GetOrderDetails(int orderId)
        {
            var order = await _context.orders
                .Where(o => o.order_id == orderId)
                .Select(o => new
                {
                    o.order_id,
                    o.order_date,
                    o.status,
                    o.total_amount,
                    o.recipient_name,
                    o.recipient_number,
                    o.address_id,
                    o.user_id,
                    o.temp_order_id
                })
                .FirstOrDefaultAsync();

            if (order == null)
                return NotFound("Order not found");

            // Fetch user name
            var user = await _context.users
                .Where(u => u.user_id == order.user_id)
                .Select(u => u.username)
                .FirstOrDefaultAsync();

            // Fetch delivery address
            var address = await _context.user_addresses
                .Where(a => a.address_id == order.address_id)
                .Select(a => new
                {
                    a.address,
                    a.city,
                    a.pincode,
                    a.address_type
                })
                .FirstOrDefaultAsync();

            // Base URL to construct full image path
            var baseUrl = $"{Request.Scheme}://{Request.Host}/";

            // Fetch ordered items with product details and full image URL
            var items = await _context.order_items
                .Where(oi => oi.order_id == orderId)
                .Join(_context.products,
                    oi => oi.product_id,
                    p => p.product_id,
                    (oi, p) => new
                    {
                        p.product_id,
                        p.product_name,
                        product_picture = string.IsNullOrEmpty(p.product_picture) ? null : baseUrl + p.product_picture,
                        p.product_price,
                        oi.quantity,
                        Subtotal = oi.quantity * p.product_price
                    })
                .ToListAsync();

            return Ok(new
            {
                OrderId = order.order_id,
                OrderDate = order.order_date,
                Status = order.status,
                TotalAmount = order.total_amount,
                RecipientName = order.recipient_name,
                RecipientNumber = order.recipient_number,
                CustomerName = user,
                ShippingAddress = address,
                Items = items
            });
        }

        [HttpPut("status/{orderId}")]
        public async Task<IActionResult> UpdateOrderStatus(int orderId, [FromBody] string status)
        {
            var order = await _context.orders.FindAsync(orderId);
            if (order == null)
                return NotFound("Order not found");

            // Update the order status
            order.status = status;
            await _context.SaveChangesAsync();

            // Fetch user info to send the email
            var user = await _context.users.FirstOrDefaultAsync(u => u.user_id == order.user_id);
            if (user != null)
            {
                string subject = $"📦 Order Status Update - Order #{order.temp_order_id}";
                string body = $@"Hi {user.username}, 👋

Your order #{order.temp_order_id} has been updated to the following status: {status}.

📅 Date: {order.order_date:f}
💰 Total: ₹{order.total_amount}

Thank you for choosing us!

— charms by Saima 💫";

                // Send email notification
                await _emailService.SendEmailAsync(user.email_id, subject, body);
            }

            return Ok("Order status updated and email sent");
        }



        [HttpDelete("{orderId}")]
        public async Task<IActionResult> DeleteOrder(int orderId)
        {
            var order = await _context.orders.FindAsync(orderId);
            if (order == null)
                return NotFound("Order not found");

            var orderItems = await _context.order_items.Where(oi => oi.order_id == orderId).ToListAsync();
            _context.order_items.RemoveRange(orderItems);
            _context.orders.Remove(order);

            await _context.SaveChangesAsync();
            return Ok("Order deleted");
        }


        [HttpGet("all")]
        public async Task<IActionResult> GetAllOrders()
        {
            var orders = await _context.orders
                .OrderByDescending(o => o.order_date)
                .Select(o => new
                {
                    o.order_id,
                    o.order_date,
                    o.status,
                    o.total_amount,
                    o.recipient_name,
                    o.recipient_number,
                    o.user_id,
                    o.temp_order_id
                })
                .ToListAsync();

            return Ok(orders);
        }

        [HttpPut("cancel/{orderId}")]
        public async Task<IActionResult> CancelOrder(int orderId)
        {
            var order = await _context.orders.FindAsync(orderId);
            if (order == null)
                return NotFound("Order not found");

            if (order.status == "Cancelled")
                return BadRequest("Order is already cancelled");

            // Fetch user info
            var user = await _context.users.FirstOrDefaultAsync(u => u.user_id == order.user_id);
            if (user != null)
            {
                string subject = "❌ Order Cancelled - justAddGelang";
                string body = $@"Hi {user.username}, 👋

We regret to inform you that your order with Order ID: {order.temp_order_id} has been cancelled.

If you have any questions or if this cancellation was a mistake, please feel free to reach out to us.

— charms by Saima 💫";

                // Send email notification
                await _emailService.SendEmailAsync(user.email_id, subject, body);
            }

            // Update order status to "Cancelled"
            order.status = "Cancelled";
            _context.orders.Update(order);

            // Restore product stock
            var orderItems = await _context.order_items
                .Where(oi => oi.order_id == orderId)
                .ToListAsync();

            foreach (var item in orderItems)
            {
                var product = await _context.products.FindAsync(item.product_id);
                if (product != null)
                {
                    product.product_stock += item.quantity;
                    _context.products.Update(product);
                }
            }

            await _context.SaveChangesAsync();

            return Ok(new { success = true, message = "Order cancelled, stock restored, and user notified" });
        }



    }



    public class OrderCreateModel
    {
        public int UserId { get; set; }
        public decimal TotalAmount { get; set; }
        public List<OrderItemModel> Items { get; set; }
    }

    public class OrderItemModel
    {
        public int ProductId { get; set; }
        public int Quantity { get; set; }
    }
    public class OrderCreateRequest
    {
        public int UserId { get; set; }
        public int AddressId { get; set; } // ✅ Required
        public string RecipientName { get; set; } // ✅ Optional but recommended
        public string RecipientNumber { get; set; } // ✅ Optional but recommended
        public decimal TotalAmount { get; set; }
        public List<OrderItemRequest> OrderItems { get; set; }

    }

    public class OrderItemRequest
    {
        public int ProductId { get; set; }
        public int Quantity { get; set; }
    }
}
