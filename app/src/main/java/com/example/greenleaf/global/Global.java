package com.example.greenleaf.global;

import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.Model.User;

public class Global {
    public static User currentUser;
    public static Livre currentBook;
    public static String currentUserId;

    public static User otherUser;
    public static String composedKey;
    public static String value_1_2;

    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECIEVE = 2;
    public static final int TYPE_DAY = 3;
    public static final String UserEmailKey = "UserEmail";
    public static final String UserPasswordKey = "UserPassword";


}
