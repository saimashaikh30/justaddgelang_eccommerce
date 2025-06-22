﻿using Microsoft.Extensions.Configuration;
using System.Net;
using System.Net.Mail;
using System.Threading.Tasks;

public interface IEmailService
{
    Task SendEmailAsync(string toEmail, string subject, string body);
}

public class EmailService : IEmailService
{
    private readonly IConfiguration _config;

    public EmailService(IConfiguration config)
    {
        _config = config;
    }

    public async Task SendEmailAsync(string toEmail, string subject, string body)
    {
        var smtpClient = new SmtpClient(_config["EmailSettings:SmtpServer"])
        {
            Port = int.Parse(_config["EmailSettings:SmtpPort"]),
            Credentials = new NetworkCredential(_config["EmailSettings:SenderEmail"], _config["EmailSettings:SenderPassword"]),
            EnableSsl = true,
        };

        var mailMessage = new MailMessage
        {
            From = new MailAddress(_config["EmailSettings:SenderEmail"]),
            Subject = subject,
            Body = body,
            IsBodyHtml = false,
        };
        mailMessage.To.Add(toEmail);

        await smtpClient.SendMailAsync(mailMessage);
    }
}
