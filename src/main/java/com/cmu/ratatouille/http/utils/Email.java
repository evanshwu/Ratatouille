package com.cmu.ratatouille.http.utils;

import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Email {
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
//    public static void main(String[] args)
//    {
//        String email = "users@example.com";
//        boolean valid = EmailValidator.getInstance().isValid(email);
//        Email myemail = new Email();
//        //boolean valid = myemail.isValidEmailAddress(email);
//        System.out.println(valid);
//    }
}