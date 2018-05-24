package jee.mif.bl.utils;

/**
 * Created by Tadas.
 */
public final class UrlUtils {

    public static final String NEW_USER_REGISTRATION = "/new-registration/";
    public static final String PASSWORD_RESET = "/password-reset/";
    public static final String ACCESS_DENIED = "/access-denied/";
    public static final String MANAGMENT = "/management/**";
    public static final String LOGIN = "/";
    public static final String LOGOUT = "/logout/";
    public static final String APPLY_REGISTRATION = "/apply-authentication/";
    public static final String FINISH_REGISTRATION = "/finish-registration/";
    public static final String SURVEY_PREVIEW = "/auth/survey-edit/preview/";
    public static final String SURVEY_DETAILS = "/survey-details/";
    public static final String SURVEY_ANSWERING = "/survey-answering/";
    public static final String SURVEY_CONTINUE_ANSWERING = "/survey-continue-answering/";
    public static final String[] PERMIT_ALL_URLS = {NEW_USER_REGISTRATION, ACCESS_DENIED, MANAGMENT, LOGIN, APPLY_REGISTRATION, FINISH_REGISTRATION};

    public static final String AUTH_END_POINT = "/auth/";
    public static final String AUTH_ACCOUNT_MANAGEMENT = "/auth/management/";
    public static final String AUTH_PASSWORD_RESET = "/auth/password-reset/";

    public static final String ADMIN_END_POINT = "/admin/";
    public static final String ADMIN_USERS = "/admin/users/";
    public static final String ADMIN_APPLY_NEW_USER = "/admin/apply-new-user/";

}
