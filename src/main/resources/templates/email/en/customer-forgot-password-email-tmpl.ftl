<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
         <title>Sending email for forgot password</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    </head>
    <body>
        <table style="background-color: #f3f3f3; width: 100%;">
            <tr>
                <td style="color: #4a4a4a;">
                    <table>
                        <tr>
                            <td>
                                <table>
                                    <tr>
                                        <td>
                                            <table>
                                                <tr>
                                                    <td>
                                                        <div>
                                                            <#setting locale="en_EN">
                                                            <p>${.now?string('dd MMMM yyyy')}</p>
                                                            <br />
                                                            <p>Dear ${name},</p>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <div>
                                                            <p>
                                                                Reset your alseyahat password by following link.
                                                            </p>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                            <p></p>
                                            <p>Sincerely yours,</p>
                                            <br />
                                            <p>Alseyahat.</p>
                                            <br />
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <p>
                                    Please note that the Alseyahat has no policy to contact you, in whatever channels, to ascertain personal information and/or any information such as username, password, account number or credit card number. If
                                    you notice any unusual transactions or communications.
                                    <br />
                                </p>

                                <hr />
                                This email is auto-generated. Please do not reply. If you have further enquiries or need more information, please contact via email.
                                <br />
                                <br />
                                Contact for Business Support: support@alseyahat.pk
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </body>
</html>
