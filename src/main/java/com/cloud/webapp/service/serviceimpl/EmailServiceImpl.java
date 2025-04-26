package com.cloud.webapp.service.serviceimpl;

import com.cloud.webapp.exceptions.Types.GeneralServiceException;
import com.cloud.webapp.service.EmailService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.cloud.webapp.utils.helpers.getCurrentTimeUtil;


@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    private String token = getCurrentTimeUtil();

    @Override
    public void sendEmail(String receiverEmail) {
        try {
            logger.info("sendEmail() Service hit started for receiverEmail: {}", receiverEmail);
            Email from = new Email(fromEmail);
            Email to = new Email(receiverEmail);
            String subject = "RE: [Application Status at Earth Y] Verify Email Address";
            String htmlContent = String.format(
                    "<!DOCTYPE html>\n" +
                            "<html>\n<head>\n" +
                            "  <meta charset=\"utf-8\">\n" +
                            "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                            "  <title>Email Confirmation</title>\n" +
                            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                            "  <style type=\"text/css\">\n" +
                            "    @media screen {\n" +
                            "      @font-face {\n" +
                            "        font-family: 'Source Sans Pro';\n" +
                            "        font-style: normal;\n" +
                            "        font-weight: 400;\n" +
                            "        src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n" +
                            "      }\n" +
                            "      @font-face {\n" +
                            "        font-family: 'Source Sans Pro';\n" +
                            "        font-style: normal;\n" +
                            "        font-weight: 700;\n" +
                            "        src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n" +
                            "      }\n" +
                            "    }\n" +
                            "    body,\n" +
                            "    table,\n" +
                            "    td,\n" +
                            "    a {\n" +
                            "      -ms-text-size-adjust: 100%%;\n" +
                            "      -webkit-text-size-adjust: 100%%;\n" +
                            "    }\n" +
                            "    table,\n" +
                            "    td {\n" +
                            "      mso-table-rspace: 0pt;\n" +
                            "      mso-table-lspace: 0pt;\n" +
                            "    }\n" +
                            "    img {\n" +
                            "      -ms-interpolation-mode: bicubic;\n" +
                            "    }\n" +
                            "    a[x-apple-data-detectors] {\n" +
                            "      font-family: inherit !important;\n" +
                            "      font-size: inherit !important;\n" +
                            "      font-weight: inherit !important;\n" +
                            "      line-height: inherit !important;\n" +
                            "      color: inherit !important;\n" +
                            "      text-decoration: none !important;\n" +
                            "    }\n" +
                            "    div[style*=\"margin: 16px 0;\"] {\n" +
                            "      margin: 0 !important;\n" +
                            "    }\n" +
                            "    body {\n" +
                            "      width: 100%% !important;\n" +
                            "      height: 100%% !important;\n" +
                            "      padding: 0 !important;\n" +
                            "      margin: 0 !important;\n" +
                            "    }\n" +
                            "    table {\n" +
                            "      border-collapse: collapse !important;\n" +
                            "    }\n" +
                            "    a {\n" +
                            "      color: #1a82e2;\n" +
                            "    }\n" +
                            "    img {\n" +
                            "      height: auto;\n" +
                            "      line-height: 100%%;\n" +
                            "      text-decoration: none;\n" +
                            "      border: 0;\n" +
                            "      outline: none;\n" +
                            "    }\n" +
                            "  </style>\n" +
                            "</head>\n<body style=\"background-color: #e9ecef;\">\n" +
                            "  <div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n" +
                            "    A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n" +
                            "  </div>\n" +
                            "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">\n" +
                            "    <tr>\n" +
                            "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" style=\"max-width: 600px;\">\n" +
                            "          <tr>\n" +
                            "            <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" +
                            "              Webapp\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "        </table>\n" +
                            "      </td>\n" +
                            "    </tr>\n" +
                            "    <tr>\n" +
                            "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" style=\"max-width: 600px;\">\n" +
                            "          <tr>\n" +
                            "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
                            "              <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "        </table>\n" +
                            "      </td>\n" +
                            "    </tr>\n" +
                            "    <tr>\n" +
                            "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" style=\"max-width: 600px;\">\n" +
                            "          <tr>\n" +
                            "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                            "              <p style=\"margin: 0;\">Tap the button below to confirm your email address and activate your account. Please delete the email if you did not request an account with the webapp.</p>\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "          <tr>\n" +
                            "            <td align=\"left\" bgcolor=\"#ffffff\">\n" +
                            "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">\n" +
                            "                <tr>\n" +
                            "                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" +
                            "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                            "                      <tr>\n" +
                            "                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" +
                            "                          <a href=\"https://deepakviswanadha.me/verify_user?username=%s&token=%s\" target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Confirm Email</a>\n" +
                            "                        </td>\n" +
                            "                      </tr>\n" +
                            "                    </table>\n" +
                            "                  </td>\n" +
                            "                </tr>\n" +
                            "              </table>\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "          <tr>\n" +
                            "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                            "              <p style=\"margin: 0;\">If you’re having trouble clicking the \"Confirm Email\" button, copy and paste the URL below into your web browser:<br> <a href=\"https://deepakviswanadha.me/verify_user?username=%s&token=%s\">https://deepakviswanadha.me/verify_user?username=%s&token=%s</a></p>\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "        </table>\n" +
                            "      </td>\n" +
                            "    </tr>\n" +
                            "    <tr>\n" +
                            "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" style=\"max-width: 600px;\">\n" +
                            "          <tr>\n" +
                            "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n" +
                            "              <p style=\"margin: 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666666;\">\n" +
                            "                &copy; 2024 Webapp. All rights reserved.<br>\n" +
                            "                This email was sent to %s.<br>\n" +
                            "              </p>\n" +
                            "            </td>\n" +
                            "          </tr>\n" +
                            "        </table>\n" +
                            "      </td>\n" +
                            "    </tr>\n" +
                            "  </table>\n" +
                            "</body>\n</html>",
                    receiverEmail, token, receiverEmail, token, receiverEmail, token, receiverEmail);

            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("sendEmail() Service finished successfully with response: {}", response);
        } catch (Exception ex) {
            logger.error("Error in sendEmail() Service: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
