package com.efedorchenko.timely.configuration.properties;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.regex.Pattern;

public class ApplicationProperties {

    public static final String BASE_PATH = "/api/v1";

    public static final String RQUID = "RqUID";
    public static final Pattern RQUID_PATTERN = Pattern.compile("^[a-zA-Z\\d-]{20,40}$");

    public static final Marker INIT = MarkerFactory.getMarker("INIT");

    public static final int DB_TIMESTAMP_PRECISION = 3;

    public static final String ROLES_CACHE_NAME = "roles";
    public static final String ROLES_BY_USER_ID_CACHE_NAME = "roles_by_user_id";
}
