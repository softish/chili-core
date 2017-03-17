package com.codingchili.realm.model;

import com.codingchili.realm.instance.model.PlayerCharacter;
import io.vertx.core.*;

import java.util.List;

import com.codingchili.core.storage.AsyncStorage;

import static com.codingchili.common.Strings.ID_ACCOUNT;
import static com.codingchili.core.configuration.CoreStrings.ID_NAME;
import static io.vertx.core.Future.failedFuture;

/**
 * @author Robin Duda
 *         <p>
 *         Storage for characters.
 */
public class CharacterDB implements AsyncCharacterStore {
    private final AsyncStorage<PlayerCharacter> characters;

    public CharacterDB(AsyncStorage<PlayerCharacter> map) {
        this.characters = map;
    }

    @Override
    public void create(Handler<AsyncResult<Void>> future, String username, PlayerCharacter character) {
        characters.putIfAbsent(character, future);
    }

    @Override
    public void findByUsername(Handler<AsyncResult<List<PlayerCharacter>>> future, String username) {
        characters.query(ID_ACCOUNT).equalTo(username).execute(future);
    }

    @Override
    public void findOne(Handler<AsyncResult<PlayerCharacter>> future, String username, String character) {
        characters.query(ID_ACCOUNT).equalTo(username)
                .and(ID_NAME).equalTo(character).execute(get -> {

            if (get.succeeded() && get.result().size() != 0) {
                future.handle(Future.succeededFuture(get.result().get(0)));
            } else {
                future.handle(Future.failedFuture(new CharacterMissingException(character)));
            }
        });
    }

    @Override
    public void remove(Handler<AsyncResult<Void>> future, String username, String character) {
        characters.query(ID_ACCOUNT).equalTo(username)
                .and(ID_NAME).equalTo(character)
                .execute(query -> {
                    if (query.succeeded() && query.result().size() > 0) {
                        characters.remove(character, future);
                    } else {
                        future.handle(failedFuture(new CharacterMissingException(character)));
                    }
                });
    }
}
