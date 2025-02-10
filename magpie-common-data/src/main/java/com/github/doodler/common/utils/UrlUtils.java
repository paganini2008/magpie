package com.github.doodler.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;
import lombok.experimental.UtilityClass;

/**
 * @Description: UrlUtils
 * @Author: Fred Feng
 * @Date: 03/04/2023
 * @Version 1.0.0
 */
@UtilityClass
public class UrlUtils {

    private static SSLSocketFactory sslSocketFactory;

    public URI toURI(String url) {
        try {
            return toURL(url).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
    }

    public URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
    }

    public URI toURI(URL baseUrl, String... paths) {
        try {
            return toURL(baseUrl, paths).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
        }
    }

    public URL toURL(URL baseUrl, String... paths) {
        Assert.notNull(baseUrl, "Base url must not be null.");
        URL url = baseUrl;
        if (ArrayUtils.isNotEmpty(paths)) {
            for (String path : paths) {
                url = toURL0(url, path);
            }
        }
        return url;
    }

    private URL toURL0(URL baseUrl, String path) {
        String part = baseUrl.getPath();
        part += checkIfStartWithSlash(path);
        try {
            return new URL(toHostUrl(baseUrl), part);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + path, e);
        }
    }

    private String checkIfStartWithSlash(String path) {
        if (!path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public URL toHostUrl(String url) {
        return toHostUrl(toURL(url));
    }

    public URL toHostUrl(URL url) {
        Assert.notNull(url, "Base url must not be null.");
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url.toString(), e);
        }
    }

    public static void saveAs(String url, int connectionTime, int readTimeout, File outputFile)
            throws IOException {
        saveAs(new URL(url), connectionTime, readTimeout, outputFile);
    }

    public static void saveAs(URL url, int connectionTime, int readTimeout, File outputFile)
            throws IOException {
        OutputStream output = null;
        try {
            output = FileUtils.openOutputStream(outputFile);
            saveAs(url, connectionTime, readTimeout, output);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    public static void saveAs(URL url, int connectionTime, int readTimeout, OutputStream output)
            throws IOException {
        InputStream ins = null;
        try {
            ins = openStream(url, connectionTime, readTimeout);
            IOUtils.copy(ins, output);
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }

    public static String toString(String url, int connectionTime, int readTimeout, Charset charset)
            throws IOException {
        return toString(toURL(url), connectionTime, readTimeout, charset);
    }

    public static String toString(URL url, int connectionTime, int readTimeout, Charset charset)
            throws IOException {
        Assert.notNull(url, "Url must be required");
        return toString(url.openConnection(), connectionTime, readTimeout, charset);
    }

    public static String toString(URLConnection url, int connectionTime, int readTimeout,
            Charset charset) throws IOException {
        return IOUtils.toString(openStream(url, connectionTime, readTimeout), charset);
    }

    public InputStream openStream(URL url, int connectionTime, int readTimeout) throws IOException {
        Assert.notNull(url, "Url must be required");
        return openStream(url.openConnection(), connectionTime, readTimeout);
    }

    public static InputStream openStream(URLConnection connection, int connectionTime,
            int readTimeout) throws IOException {
        Assert.notNull(connection, "URLConnection must be required");
        connection.setConnectTimeout(connectionTime);
        connection.setReadTimeout(readTimeout);
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        }
        if (connection instanceof HttpsURLConnection) {
            initUnsecuredTSL();
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
        }
        return connection.getInputStream();
    }

    public boolean testConnection(String url, int timeout, int statusCode) throws IOException {
        return testConnection(url, timeout, timeout, statusCode);
    }

    public boolean testConnection(String url, int connectionTimeout, int readTimeout,
            int statusCode) throws IOException {
        return testConnection(toURL(url), connectionTimeout, readTimeout, statusCode);
    }

    public boolean testConnection(URL url, int timeout, int statusCode) throws IOException {
        return testConnection(url, timeout, timeout, statusCode);
    }

    public boolean testConnection(URL url, int connectionTimeout, int readTimeout, int statusCode)
            throws IOException {
        Assert.notNull(url, "Url must be required");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        }
        if (connection instanceof HttpsURLConnection) {
            initUnsecuredTSL();
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
        }
        connection.connect();
        return connection.getResponseCode() == statusCode;
    }

    private HostnameVerifier getInsecureVerifier() {
        return new HostnameVerifier() {

            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
    }

    private synchronized void initUnsecuredTSL() throws IOException {
        if (sslSocketFactory == null) {
            sslSocketFactory = defaultSSLSocketFactory();
        }
    }

    public SSLSocketFactory defaultSSLSocketFactory() throws IOException {
        final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {

            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {}

            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {}

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Can't create unsecure trust manager.", e);
        } catch (KeyManagementException e) {
            throw new IOException("Can't create unsecure trust manager.", e);
        }
    }

    public String getProtocol(String url) {
        try {
            return new URL(url).getProtocol();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getDomainName(String url) {
        String host;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
        String[] args = host.split("\\.");
        if (args.length == 2) {
            return args[0];
        } else if (args.length == 3) {
            return args[1];
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // saveAs("https://img.dyn123.com/images/slot-images/belatra/bingopower.png", 10000, 60000,
        // new File("g:/abc/bingopower11.png"));
        System.out.println(getDomainName("https://img.dyn123.com/images"));
    }
}
