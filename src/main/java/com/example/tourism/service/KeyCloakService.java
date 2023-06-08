package com.example.tourism.service;

import com.example.tourism.entity.User;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeyCloakService {
    UserRepresentation createUser(User user);
}
