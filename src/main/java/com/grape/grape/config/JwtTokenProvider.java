//package com.grape.grape.config;
//
//import com.grape.grape.entity.User;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Component
//public class JwtTokenProvider {
//
//    private String secret = "yourSecretKey";
//
//    public String generateToken(String username) {
//
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, username);
//    }
//
//    private String createToken(Map<String, Object> claims, String subject) {
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
//                .signWith(SignatureAlgorithm.HS256, secret)
//                .compact();
//    }
//
//    public Boolean validateToken(String token, String username) {
//
//        final String extractedUsername = getUsernameFromToken(token);
//        return (extractedUsername.equals(username) && !isTokenExpired(token));
//    }
//
//    public String getUsernameFromToken(String token) {
//
//        return getClaimFromToken(token, Claims::getSubject);
//    }
//
//    public Date getExpirationDateFromToken(String token) {
//
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//
//    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims getAllClaimsFromToken(String token) {
//
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Boolean isTokenExpired(String token) {
//
//        final Date expiration = getExpirationDateFromToken(token);
//        return expiration.before(new Date());
//    }
//}