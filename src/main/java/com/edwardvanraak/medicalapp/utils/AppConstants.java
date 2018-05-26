package com.edwardvanraak.medicalapp.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HNWeb-01 on 6/2/2016.
 */
public class AppConstants {


    public static final String REGISTRATION_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/user_register.php";
    public static final String LOGIN_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/user_login.php";
    //public static final String PINVERIFY_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/match_qr_pin.php";
    public static final String PINVERIFY_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/match_qr_pin.php";
    public static final String EMAILVERIFY_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/verifiy_email.php?";
    public static final String FORGOTPASSWORD_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/forgot_password.php";


    /*********************************ADD New Info************************************************/

    public static final String ADD_NEW_INFO_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/add-new-info.php";

    /*************************************Medical Year List*************************************************************/

    public static final String MEDICAL_YEAR_LIST_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/medical-year.php";

    /************************************Medical Disease List*********************************************/
    public static final String MEDICAL_DISEASE_LIST_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/list-of-diesease.php";


    /**********************************Disease Details***********************************************************/
    public static final String MEDICAL_DISEASE_DETAILS_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/diesease-details.php";


    /****************************************Profile Details****************************************************************/

    public static final String PROFILE_DETAILS_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/view_user_profile.php";
    public static final String PROFILE_UPDATE_URL = "http://tech599.com/tech599.com/johnvij/medicalapp/user_profile_edit.php";


    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}
