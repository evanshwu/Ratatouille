package com.cmu.ratatouille.http.utils;

import com.cmu.ratatouille.models.Gender;

public class StringUtil {
    public static Gender convertToGender(String str){
        if(str.equals("male")){
            return Gender.MALE;
        }else{
            return Gender.FEMALE;
        }
    }
}
