package com.codingchili.core.Authentication.Client;

import com.codingchili.core.Authentication.Configuration.AuthProvider;
import com.codingchili.core.Authentication.Controller.ClientHandler;
import com.codingchili.core.Authentication.Model.Account;
import com.codingchili.core.Authentication.Model.AsyncAccountStore;
import com.codingchili.core.Authentication.Model.ProviderMock;
import com.codingchili.core.Configuration.Strings;
import com.codingchili.core.Protocols.ResponseStatus;
import com.codingchili.core.Protocols.Util.Serializer;
import com.codingchili.core.Protocols.Util.Token;
import com.codingchili.core.Protocols.Util.TokenFactory;
import com.codingchili.core.Realm.Configuration.RealmSettings;
import com.codingchili.core.Realm.Instance.Model.PlayerCharacter;
import com.codingchili.core.Realm.Instance.Model.PlayerClass;
import com.codingchili.core.Shared.RequestMock;
import com.codingchili.core.Shared.ResponseListener;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.codingchili.core.Configuration.Strings.*;

/**
 * @author Robin Duda
 *         tests the API from client -> authentication server.
 */

@RunWith(VertxUnitRunner.class)
public class ClientHandlerTest {
    private static final String CHARACTER_NAME = "character";
    private static final String CHARACTER_NAME_DELETED = "character-deleted";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String USERNAME_NEW = "new-username";
    private static final String USERNAME_MISSING = "missing-username";
    private static final String PASSWORD_WRONG = "wrong-password";
    private static final String REALM_NAME = "realmName.name";
    private static final String CLASS_NAME = "class.name";
    private static TokenFactory clientToken;
    private static AuthProvider provider;
    private static ClientHandler handler;

    @Rule
    public Timeout timeout = new Timeout(5, TimeUnit.SECONDS);

    @Before
    public void setUp(TestContext context) throws IOException {
        RealmSettings realm = new RealmSettings()
                .setTemplate(new PlayerCharacter().setName("realm 1").setClassName(CLASS_NAME))
                .setName(REALM_NAME)
                .setType("test-type")
                .setResources("ao.pathing.node");

        ArrayList<PlayerClass> classes = new ArrayList<>();
        classes.add(new PlayerClass().setName(CLASS_NAME));

        realm.setClasses(classes);
        realm.getAuthentication().setToken(new Token().setKey("test-key"));

        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(ID_DESCRIPTION, "Text description :) ");
        realm.setAttributes(attributes);

        provider = new ProviderMock();
        provider.getRealmStore().put(Future.future(), realm);

        handler = new ClientHandler(provider);

        clientToken = provider.getClientTokenFactory();
        addAccount(context);
    }

    private static void addAccount(TestContext context) {
        Async async = context.async();
        AsyncAccountStore accounts = provider.getAccountStore();
        PlayerCharacter add = new PlayerCharacter().setName(CHARACTER_NAME);
        PlayerCharacter delete = new PlayerCharacter().setName(CHARACTER_NAME_DELETED);

        Future<Account> future = Future.future();

        future.setHandler(result -> {
            accounts.upsertCharacter(Future.future(), REALM_NAME, USERNAME, add);
            accounts.upsertCharacter(Future.future(), REALM_NAME, USERNAME, delete);
            async.complete();
        });

        accounts.register(future, new Account(USERNAME, PASSWORD));
    }


    @Test
    public void authenticateAccount(TestContext context) {
        Async async = context.async();

        handle(CLIENT_AUTHENTICATE, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);
            async.complete();
        }, account(USERNAME, PASSWORD));
    }

    @Test
    public void failtoAuthenticateAccountWithWrongPassword(TestContext context) {
        Async async = context.async();

        handle(CLIENT_AUTHENTICATE, (response, status) -> {
            context.assertEquals(ResponseStatus.UNAUTHORIZED, status);
            async.complete();
        }, account(USERNAME, PASSWORD_WRONG));
    }

    @Test
    public void failtoAuthenticateAccountWithMissing(TestContext context) {
        Async async = context.async();

        handle(CLIENT_AUTHENTICATE, (response, status) -> {
            context.assertEquals(ResponseStatus.MISSING, status);
            async.complete();
        }, account(USERNAME_MISSING, PASSWORD));
    }

    @Test
    public void registerAccount(TestContext context) {
        Async async = context.async();

        handle(CLIENT_REGISTER, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);
            async.complete();
        }, account(USERNAME_NEW, PASSWORD));
    }

    private JsonObject account(String username, String password) {
        return new JsonObject().put(ID_ACCOUNT, new JsonObject().put("username", username).put("password", password));
    }

    @Test
    public void failRegisterAccountExists(TestContext context) {
        Async async = context.async();

        handle(CLIENT_REGISTER, (response, status) -> {
            context.assertEquals(ResponseStatus.CONFLICT, status);
            async.complete();
        }, account(USERNAME, PASSWORD));
    }

    @Test
    public void retrieveRealmList(TestContext context) {
        Async async = context.async();
        String[] keys = {ID_NAME, ID_RESOURCES, ID_TYPE, ID_VERSION, ID_REMOTE, ID_PLAYERS, ID_SIZE, ID_ATTRIBUTES};

        handle(CLIENT_REALM_LIST, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);

            JsonArray list = response.getJsonArray(ID_REALMS);

            for (int i = 0; i < list.size(); i++) {
                JsonObject realm = list.getJsonObject(i);

                for (String key : keys)
                    context.assertTrue(realm.containsKey(key));
            }

            async.complete();
        });
    }

    @Test
    public void removeCharacter(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_REMOVE, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);
            async.complete();
        }, new JsonObject()
                .put(ID_CHARACTER, CHARACTER_NAME_DELETED)
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    private JsonObject getClientToken() {
        return Serializer.json(new Token(clientToken, USERNAME));
    }

    @Test
    public void failToRemoveMissingCharacter(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_REMOVE, (response, status) -> {
            context.assertEquals(ResponseStatus.ERROR, status);
            async.complete();
        }, new JsonObject()
                .put(ID_CHARACTER, CHARACTER_NAME + ".MISSING")
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void createCharacter(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_CREATE, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);
            async.complete();
        }, new JsonObject()
                .put(ID_CHARACTER, CHARACTER_NAME + ".NEW")
                .put(ID_CLASS, CLASS_NAME)
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void failOverwriteExistingCharacter(TestContext context) {
        Async async = context.async();
        handle(CLIENT_CHARACTER_CREATE, (response, status) -> {
            context.assertEquals(ResponseStatus.CONFLICT, status);
            async.complete();
        }, new JsonObject()
                .put(ID_CHARACTER, CHARACTER_NAME)
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void listCharactersOnRealm(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_LIST, (response, status) -> {
            context.assertEquals(ResponseStatus.ACCEPTED, status);
            context.assertTrue(characterInJsonArray(CHARACTER_NAME, response.getJsonArray(ID_CHARACTERS)));
            async.complete();
        }, new JsonObject()
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    private boolean characterInJsonArray(String charname, JsonArray characters) {
        Boolean found = false;

        for (int i = 0; i < characters.size(); i++) {
            if (characters.getJsonObject(i).getString(ID_NAME).equals(charname))
                found = true;
        }
        return found;
    }

    @Test
    public void realmDataOnCharacterList(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_LIST, (response, status) -> {
            JsonObject realm = response.getJsonObject(ID_REALM);

            context.assertEquals(ResponseStatus.ACCEPTED, status);
            context.assertTrue(realm.containsKey(ID_CLASSES));
            context.assertTrue(realm.containsKey(ID_NAME));
            context.assertTrue(realm.containsKey(ID_AFFLICTIONS));
            context.assertTrue(realm.containsKey(ID_TEMPLATE));

            async.complete();
        }, new JsonObject()
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void realmDataDoesNotIncludeTokenOnCharacterList(TestContext context) {
        Async async = context.async();

        handle(CLIENT_CHARACTER_LIST, (response, status) -> {
            JsonObject realm = response.getJsonObject(ID_REALM);

            context.assertEquals(ResponseStatus.ACCEPTED, status);
            context.assertFalse(realm.containsKey(ID_AUTHENTICATION));
            context.assertFalse(realm.containsKey(ID_TOKEN));

            async.complete();
        }, new JsonObject()
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void createRealmToken(TestContext context) {
        Async async = context.async();

        handle(CLIENT_REALM_TOKEN, (response, status) -> {
            Token token = Serializer.unpack(response, Token.class);

            context.assertEquals(ResponseStatus.ACCEPTED, status);
            context.assertEquals(USERNAME, token.getDomain());

            async.complete();
        }, new JsonObject()
                .put(ID_TOKEN, getClientToken())
                .put(ID_REALM, REALM_NAME));
    }

    @Test
    public void failCreateRealmTokenWhenInvalidToken(TestContext context) {
        handle(Strings.CLIENT_REALM_TOKEN, (response, status) -> {
            context.assertEquals(ResponseStatus.UNAUTHORIZED, status);
        }, new JsonObject()
                .put(ID_TOKEN, getInvalidClientToken()));
    }

    @Test
    public void failListCharactersOnRealmWhenInvalidToken(TestContext context) {
        handle(Strings.CLIENT_CHARACTER_LIST, (response, status) -> {
            context.assertEquals(ResponseStatus.UNAUTHORIZED, status);
        }, new JsonObject()
                .put(ID_TOKEN, Serializer.json(getInvalidClientToken())));
    }

    @Test
    public void failToCreateCharacterWhenInvalidToken(TestContext context) {
        handle(Strings.CLIENT_CHARACTER_CREATE, (response, status) -> {
            context.assertEquals(ResponseStatus.UNAUTHORIZED, status);
        }, new JsonObject()
                .put(ID_TOKEN, getInvalidClientToken()));
    }

    @Test
    public void failToRemoveCharacterWhenInvalidToken(TestContext context) {
        handle(Strings.CLIENT_CHARACTER_REMOVE, (response, status) -> {
            context.assertEquals(ResponseStatus.UNAUTHORIZED, status);
        }, new JsonObject()
                .put(ID_TOKEN, getInvalidClientToken()));
    }

    @Test
    public void testPingClientHandler(TestContext context) {
        handle(ID_PING, ((response, status) -> {
            context.assertEquals(status, ResponseStatus.ACCEPTED);
        }));
    }

    private JsonObject getInvalidClientToken() {
        return Serializer.json(new Token(new TokenFactory("invalid".getBytes()), "username"));
    }

    private void handle(String action, ResponseListener listener) {
        handle(action, listener, new JsonObject().put(ID_TOKEN, getClientToken()));
    }

    private void handle(String action, ResponseListener listener, JsonObject data) {
        handler.process(RequestMock.get(action, listener, data));
    }
}
