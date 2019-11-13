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
        String str = "1@12334322.com";
        String str2 = "phil@andrew.cmu.edu";

        System.out.println(checkEmail(str));
        System.out.println(checkEmail(str2));
    }
}

