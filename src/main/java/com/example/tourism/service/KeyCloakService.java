package com.example.tourism.service;

import com.example.tourism.payLoad.request.UserRequest;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeyCloakService {
    UserRepresentation createUser(UserRequest userRequest);
}
