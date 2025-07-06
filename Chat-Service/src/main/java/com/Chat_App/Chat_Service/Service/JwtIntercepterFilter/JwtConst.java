package com.Chat_App.Chat_Service.Service.JwtIntercepterFilter;

import java.util.Base64;

public class JwtConst {

    public static final String SECURITY_KEY = "dfkdhfdnvlkndfne673453474gfbivlvkjoibhjjhXTYTUYIGUHOBTYFYIy7t68tibjkbu^*IYBGJVYGol";

    public static final String CONSTANT_KEY = Base64.getEncoder().encodeToString("1234567890123456".getBytes());

    public static final String JWT_HEADER = "Authorization";

}
