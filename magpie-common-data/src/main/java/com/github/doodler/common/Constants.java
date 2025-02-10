package com.github.doodler.common;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @Description: Constants
 * @Author: Fred Feng
 * @Date: 30/10/2024
 * @Version 1.0.0
 */
public interface Constants {

    String PROJECT_NAME = "Doodler";
    String VERSION = "1.0.0-SNAPSHOT";
    String DEFAULT_CLUSTER_NAME = "mycluster";

    String NEWLINE = System.getProperty("line.separator");

    int SERVER_PORT_FROM = 50000;
    int SERVER_PORT_TO = 60000;

    String REQUEST_HEADER_REQUEST_ID = "__request_id__";
    String REQUEST_HEADER_TIMESTAMP = "__timestamp__";
    String REQUEST_HEADER_TRACES = "__traces__";
    String REQUEST_HEADER_TRACE_ID = "__trace_id__";
    String REQUEST_HEADER_SPAN_ID = "__span_id__";
    String REQUEST_HEADER_PARENT_SPAN_ID = "__parent_span_id__";
    String REQUEST_HEADER_API_REALM = "__api__";

    Integer COMMON_VALID = 1;
    Integer COMMON_INVALID = 0;
    Integer COMMON_EXPIRED = -1;

    long DEFAULT_MAXIMUM_RESPONSE_TIME = 3L * 1000;

    String REQUEST_HEADER_ENDPOINT_SECURITY_KEY = "ENDPOINT_SECURITY_KEY";
    String REQUEST_HEADER_REST_CLIENT_SECURITY_KEY = "REST_CLIENT_SECURITY_KEY";

    String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String[] SUPPORTED_DATE_TIME_PATTERNS = {"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.S",
            "yyyy-MM-dd'T'HH:mm:ss.SXXX", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd.HH:mm:ss", "yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy", "dd/MMM/yyyy",
            "yyyyMMddHHmmss", "yyyyMMdd", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss Z"};

    String URL_PATTERN_PING = "%s://%s:%d%s/ping";

    Integer CURRENCY_RATE_CACHE_MINUTES = 10;
    String CACHE_KEY_CURRENCY_RATE = "currency_rate_%s_%s";
    String CACHE_KEY_USER_ID = "cache_user_id_%s";

    String CURRENCY_USD = "USD";
    String CURRENCY_USDT = "USDT";

    String CURRENCY_TYPE_CRYPTO = "c";
    String CURRENCY_TYPE_FIAT = "f";

    String REDIS_CACHE_NAME_PREFIX_PATTERN = "doodler:%s:";
    String ENC_PATTERN = "ENC('%s')";

    String DEFAULT_SERVER_SECURITY_KEY = "5h0E5GZ3DhAJTSQOFQhlxEZEgJYFjdQz";
    String REQUEST_INFO_CHECKING_PERMISSIONS = "permissions";
    String PLATFORM_WEBSITE = "website";
    String PLATFORM_ADMIN = "admin";

    String USER_ATTRIBUTE_MY_PROFILE = "MY_PROFILE";
    String PUBSUB_CHANNEL_FANOUT_CHAT_ENABLED = "FANOUT-CHAT-ENABLED";

    String REDIS_KEY_PATTERN_MAINTENANCE = "maintenance";

    DateTimeFormatter DTF_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateTimeFormatter DTF_YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    DateTimeFormatter DTF_YMDHMS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    String ENV_TEST = "test";
    String ENV_PROD = "prod";

    String GLOBAL_APPLICATION_EVENT_ONLINE = "APPLICATION_ONLINE";

    String GLOBAL_APPLICATION_EVENT_OFFLINE = "APPLICATION_OFFLINE";

    List<String> userAgents = Collections.unmodifiableList(Arrays.asList(new String[] {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Opera/9.80 (Windows NT 6.1; U; zh-cn) Presto/2.9.168 Version/11.50",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB7.0)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.33 Safari/534.3 SE 2.X MetaSr 1.0",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0"}));
}
