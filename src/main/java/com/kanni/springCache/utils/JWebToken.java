package com.kanni.springCache.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.kanni.springCache.exception.JWTException;
import com.kanni.springCache.model.JWTTokenInfo;
import com.kanni.springCache.service.impl.UserDetailsImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


@Component
public class JWebToken {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JWebToken.class);


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final String ISSUER = "kk@domain.com";
    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private JSONObject payload = new JSONObject();
    private String signature;
    private String encodedHeader;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;


    public String generateJwtToken(UserDetailsImpl userDetails) {

        encodedHeader = encode(new JSONObject(JWT_HEADER));
        LocalDateTime validity = LocalDateTime.now().plusHours(5);
        payload.put("sub", userDetails.getUsername());
        payload.put("auth", userDetails.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList()));
        payload.put("exp", validity.toEpochSecond(ZoneOffset.UTC));
        payload.put("email", userDetails.getEmail());
        payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        payload.put("iss", ISSUER);
        payload.put("jti", UUID.randomUUID().toString()); //how do we use this?
        signature=hmacSha256(encodedHeader + "." + encode(payload), secretKey);
        return encodedHeader + "." + encode(payload) + "." + signature;
    }


    public void verifyToken(String token) {
        encodedHeader = encode(new JSONObject(JWT_HEADER));
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Token format");
        }
        if (encodedHeader.equals(parts[0])) {
            encodedHeader = parts[0];
        } else {
            throw new JWTException("JWT Header is Incorrect: " + parts[0]);
        }

        payload = new JSONObject(decode(parts[1]));
        if (payload.isEmpty()) {
            throw new JSONException("Payload is Empty: ");
        }
        if (!payload.has("exp")) {
            throw new JSONException("Payload doesn't contain expiry " + payload);
        }
        signature = parts[2];
    }

    @Cacheable(cacheNames = "JWTTokenInfo", key = "#token")
    public JWTTokenInfo getTokenInfo(String token)  {
        LOGGER.info("Create entry in Cache.....");
        verifyToken(token);
        if (null != payload) {
            JWTTokenInfo jwtTokenInfo = new JWTTokenInfo();
            jwtTokenInfo.setUserName(payload.getString("sub"));
            jwtTokenInfo.setIssuer(payload.getString("iss"));
            jwtTokenInfo.setRoles(getAudience());
            jwtTokenInfo.setEmail(payload.getString("email"));
            return jwtTokenInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return encodedHeader + "." + encode(payload) + "." + signature;
    }

    public boolean isValid() {
        return payload.getLong("exp") > (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) //token not expired
                && signature.equals(hmacSha256(encodedHeader + "." + encode(payload), secretKey)); //signature matched
    }

    public String getSubject() {
        return payload.getString("sub");
    }

    public List<String> getAudience() {
        JSONArray arr = payload.getJSONArray("auth");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    private static String encode(JSONObject obj) {
        return encode(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }


    private String hmacSha256(String data, String secret) {
        try {

            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(JWebToken.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

}