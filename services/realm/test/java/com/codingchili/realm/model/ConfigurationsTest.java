package com.codingchili.realm.model;

import com.codingchili.core.files.JsonFileStore;
import com.codingchili.core.files.exception.NoSuchResourceException;
import com.codingchili.core.protocol.Serializer;
import com.codingchili.realm.instance.model.Affliction;
import com.codingchili.realm.instance.model.Inventory;
import com.codingchili.realm.instance.model.PlayerCharacter;
import com.codingchili.realm.instance.model.PlayerClass;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codingchili.core.configuration.CoreStrings.testDirectory;
import static com.codingchili.core.configuration.CoreStrings.testFile;

/**
 * @author Robin Duda
 * tests the loading of json files used for configuration storage.
 */
@RunWith(VertxUnitRunner.class)
public class ConfigurationsTest {

    @Rule
    public Timeout timeout = new Timeout(5, TimeUnit.SECONDS);

    @Test
    public void testReadList() throws IOException {
        JsonFileStore.readList(testFile("affliction.json"));
    }

    @Test
    public void readMissingFile(TestContext test) {
        try {
            JsonFileStore.readObject("missing/file.json");
            test.fail("No exception on missing file.");
        } catch (NoSuchResourceException ignored) {
        }
    }

    @Test
    public void readDirectoryObjects() throws IOException {
        JsonFileStore.readDirectoryObjects(testDirectory("class"));
    }

    @Test
    public void testReadAfflictions() throws IOException {
        JsonArray afflictions = JsonFileStore.readList(testFile("affliction.json"));

        for (int i = 0; i < afflictions.size(); i++) {
            Serializer.unpack(afflictions.getJsonObject(i), Affliction.class);
        }

        Assert.assertFalse(afflictions.isEmpty());
    }

    @Test
    public void testReadPlayerClasses() throws IOException {
        List<JsonObject> classes = JsonFileStore.readDirectoryObjects(testDirectory("class"));

        for (JsonObject player : classes) {
            Serializer.unpack(player, PlayerClass.class);
        }

        Assert.assertFalse(classes.isEmpty());
    }

    @Test
    public void testReadPlayerTemplate() throws IOException {
        JsonObject template = JsonFileStore.readObject(testFile("character.json"));
        PlayerCharacter player = Serializer.unpack(template, PlayerCharacter.class);

        Assert.assertNotNull(player);
        Assert.assertFalse(player.getAttributes().isEmpty());

        Inventory inventory = player.getInventory();
        Assert.assertFalse(inventory.getEquipped().isEmpty());
        Assert.assertFalse(inventory.getItems().isEmpty());
    }
}
