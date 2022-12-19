package com.kanni.springCache.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class JWTTokenInfo {

    private String userName;

  //  private String role;

    private Date expiryDate;

    private String issuer;

    private String email;

    private List<String> roles;
}
