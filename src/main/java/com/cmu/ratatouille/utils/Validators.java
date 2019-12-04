package com.cmu.ratatouille.utils;

import org.apache.commons.validator.routines.EmailValidator;

class Validators {

    public static boolean checkEmail(String str){
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(emailValidator.isValid(str)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String str = "123";
        String str2 = "phil@andrew.cmu.edu";
        String str3 = "123gmail.com";

        System.out.println(checkEmail(str));
        System.out.println(checkEmail(str2));
        System.out.println(checkEmail(str3));
    }
}
