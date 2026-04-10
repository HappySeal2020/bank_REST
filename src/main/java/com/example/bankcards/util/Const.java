package com.example.bankcards.util;

public final class Const {
    //common path
    public static final String REST_MAP = "/api-1.0";

    //security
    public static final String AUTH_PATH = "/auth";

    public static final String REST_LOGIN = "/login";
    public static final String REST_REFRESH = "/refresh";

    //paths for Admin
    public static final String REST_CARD = "/cards";
    public static final String REST_CARD_STATUS = REST_CARD+"/status";
    public static final String REST_USER = "/users";

    //paths for client
    public static final String REST_CLIENT = "/client";
    public static final String REST_CLIENT_CARDSTATUS = REST_CLIENT+"/cardstatus";
    public static final String REST_CLIENT_TRANSFER = REST_CLIENT+"/transfer";
    public static final String REST_CLIENT_CHANGEBALANCE = REST_CLIENT+"/changebalance";

}
