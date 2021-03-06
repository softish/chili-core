package com.codingchili.realmregistry;

import com.codingchili.core.context.StorageContext;
import com.codingchili.core.context.SystemContext;
import com.codingchili.core.security.Token;
import com.codingchili.core.security.TokenFactory;
import com.codingchili.core.storage.StorageLoader;
import com.codingchili.realmregistry.configuration.RealmRegistrySettings;
import com.codingchili.realmregistry.configuration.RegisteredRealm;
import com.codingchili.realmregistry.configuration.RegistryContext;
import com.codingchili.realmregistry.model.AsyncRealmStore;
import com.codingchili.realmregistry.model.RealmDB;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * @author Robin Duda
 */
public class ContextMock extends RegistryContext {

    public ContextMock(Vertx vertx) {
        super(new SystemContext(vertx));

        this.realmFactory = new TokenFactory(new RealmRegistrySettings().getRealmSecret());

        new StorageLoader<RegisteredRealm>().privatemap(new StorageContext<>(this))
                .withClass(RegisteredRealm.class)
                .withDB("", "")
                .build(result -> {
                    this.realms = new RealmDB(result.result());
                });

        RegisteredRealm realm = new RegisteredRealm()
                .setName("realmName")
                .setAuthentication(new Token(new TokenFactory("s".getBytes()), "realmName"));


        realms.put(Future.future(), realm);
    }

    @Override
    public AsyncRealmStore getRealmStore() {
        return realms;
    }

    public TokenFactory getClientFactory() {
        return new TokenFactory(new RealmRegistrySettings().getClientSecret());
    }
}
