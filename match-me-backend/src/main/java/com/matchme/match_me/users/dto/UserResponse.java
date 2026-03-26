package com.matchme.match_me.users.dto;

 //response for /users/{id} endpoint
 //email is private and must not be exposed
 
public record UserResponse(
        Long id,
        String name,
        String profilePictureUrl
) {}
