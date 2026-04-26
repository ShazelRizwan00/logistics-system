package com.logistics.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret; //JWT signing key used to send and validate tokens
    //1 day in milliseconds
    private long expirationMs = 86_400_000L;  //key expires after 1 day
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}
//Configuration holder for JWT settings with automatic property binding