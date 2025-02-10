package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @Description: Coookie
 * @Author: Fred Feng
 * @Date: 11/01/2025
 * @Version 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class Coookie implements Serializable {

    private static final long serialVersionUID = -8057227466282494897L;
    private String domain;
    private String name;
    private String value;
    private String path;
    private int maxAge;
    private Date expires;
    private boolean secure;
    private boolean httpOnly;

    public Coookie(String domain, String name, String value) {
        if (domain == null) {
            throw new IllegalArgumentException("Cookie domain must be specified");
        }
        this.name = name;
        this.value = value;
    }

    public Coookie(String domain, String name, String value, String path, Date expires,
            boolean secure, boolean httpOnly) {
        this(domain, name, value);
        this.path = path;
        this.expires = expires;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public Coookie(String domain, String name, String value, String path, int maxAge,
            boolean secure, boolean httpOnly) {
        this(domain, name, value);
        this.path = path;
        if (maxAge < -1) {
            throw new IllegalArgumentException("Invalid max age:  " + maxAge);
        }
        if (maxAge >= 0) {
            this.expires = new Date(System.currentTimeMillis() + (maxAge * 1000L));
        }
        this.maxAge = maxAge;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        return getName() + "=" + getValue() + (getDomain() == null ? "" : ";domain=" + getDomain())
                + (getPath() == null ? "" : ";path=" + getPath())
                + (getExpires() == null ? "" : ";expires=" + getExpires())
                + (isSecure() ? ";secure" : "") + (isHttpOnly() ? ";httpOnly" : "");
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Coookie)) {
            return false;
        }
        final Coookie other = (Coookie) o;
        final String path = getPath() == null ? "/" : getPath();
        final String otherPath = other.getPath() == null ? "/" : other.getPath();
        return new EqualsBuilder().append(getName(), other.getName())
                .append(getDomain(), other.getDomain()).append(path, otherPath).isEquals();
    }

    @Override
    public int hashCode() {
        final String path = getPath() == null ? "/" : getPath();
        return new HashCodeBuilder().append(getName()).append(getDomain()).append(path)
                .toHashCode();
    }

}
