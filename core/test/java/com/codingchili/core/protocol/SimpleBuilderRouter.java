package com.codingchili.core.protocol;

import com.codingchili.core.listener.CoreHandler;
import com.codingchili.core.listener.Request;
import com.codingchili.core.testing.StorageObject;

import static com.codingchili.core.protocol.ProtocolTest.*;
import static com.codingchili.core.protocol.Role.ADMIN;
import static com.codingchili.core.protocol.Role.USER;

public class SimpleBuilderRouter implements CoreHandler {
    private Protocol<Request> protocol = new Protocol<>();

    public SimpleBuilderRouter() {
        protocol.setDescription(ProtocolTest.DOCSTRING_TEXT)
                .setDataModel(StorageObject.class)
                .setRole(Role.PUBLIC)
                    .use(documentedRoute, this::documentedRoute)
                        .document(DOC_PUBLIC)
                    .use(defaultRolePublic, this::defaultRolePublic)
                        .model(StorageObject.class)
                    .use(adminRoleRoute, this::adminRoleRoute, ADMIN)
                    .use(userRoleRoute, this::userRoleRoute, USER)
                    .use(specialRoute, this::customRouteName)
                    .use(multipleUserRoute, this::multipleUserRoute, USER, ADMIN)
                    .use(customRoleOnRoute, this::customRoleOnRoute, RoleMap.get(CUSTOM_ROLE));
    }

    public Protocol<Request> getProtocol() {
        return protocol;
    }

    public void documentedRoute(Request request) {
        request.write(documentedRoute);
    }

    public void defaultRolePublic(Request request) {
        request.write(defaultRolePublic);
    }

    public void adminRoleRoute(Request request) {
        request.write(adminRoleRoute);
    }

    public void userRoleRoute(Request request) {
        request.write(userRoleRoute);
    }

    public void customRouteName(Request request) {
        request.write(specialRoute);
    }

    public void multipleUserRoute(Request request) {
        request.write(multipleUserRoute);
    }

    public void customRoleOnRoute(Request request) {
        request.write(customRoleOnRoute);
    }

    @Override
    public void handle(Request request) {
        // not used: protocol is called directly when testing.
    }

    @Override
    public String address() {
        return ProtocolTest.ADDRESS;
    }
}
