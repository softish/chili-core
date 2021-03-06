package com.codingchili.realmregistry.configuration;

import com.codingchili.core.security.Token;
import com.codingchili.core.storage.Storable;

/**
 * @author Robin Duda
 * <p>
 * Contains information about a realm, received from a realmserver..
 */
public class RegisteredRealm implements Storable {
    private Token authentication;
    private String description;
    private String resources;
    private String remote;
    private String version;
    private int size;
    private String type;
    private String lifetime;
    private int players = 0;
    private Boolean trusted;
    private Boolean secure;
    private long updated;
    private String name;

    @Override
    public String id() {
        return name.toLowerCase();
    }

    /**
     * @return get the remote handler of the server.
     */
    public String getRemote() {
        return remote;
    }

    /**
     * @param remote set the remote handler of the server.
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }

    /**
     * @return returns the authentication token for the realm.
     */
    public Token getAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the authentication token to set for the realm.
     * @return fluent
     */
    public RegisteredRealm setAuthentication(Token authentication) {
        this.authentication = authentication;
        return this;
    }

    /**
     * @return the realm description as a string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the realm description as a string.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the resource server for the realm.
     */
    public String getResources() {
        return resources;
    }

    /**
     * @param resources set the resource server for the realm.
     */
    public void setResources(String resources) {
        this.resources = resources;
    }

    /**
     * @return get the version of the realm.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version set the version of the realm.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return maximum number of players that may connect to the server.
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size sets the maximum number of players that may connect to the server.
     * @return fluent
     */
    public RegisteredRealm setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * @return type of the realm, a description.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type sets the type description of the realm.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the lifetime of the realm.
     */
    public String getLifetime() {
        return lifetime;
    }

    /**
     * @param lifetime sets the lifetime of the realm.
     */
    public void setLifetime(String lifetime) {
        this.lifetime = lifetime;
    }

    /**
     * @return get the number of connected players.
     */
    public int getPlayers() {
        return players;
    }

    /**
     * @param players set the number of connected players.
     */
    public void setPlayers(int players) {
        this.players = players;
    }

    /**
     * @return true if the realm is trusted.
     */
    public Boolean getTrusted() {
        return trusted;
    }

    /**
     * @param trusted indicates if the realm is trusted, non third-party server.
     */
    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    /**
     * @return true if the connection is secure.
     */
    public Boolean getSecure() {
        return secure;
    }

    /**
     * @param secure indicates if the connection to the server must be secured.
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * @return the epoch ms in which the realm was last updated.
     */
    public long getUpdated() {
        return updated;
    }

    /**
     * @param updated sets the time in epoch ms when the realm was last updated in the registry.
     * @return fluent
     */
    public RegisteredRealm setUpdated(long updated) {
        this.updated = updated;
        return this;
    }

    /**
     * @return the name of the realm.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the new name of the realm.
     * @return fluent
     */
    public RegisteredRealm setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public String toString() {
        return "name=" + name + " description=" + description;
    }

    @Override
    public boolean equals(Object other) {
        return compareTo(other) == 0;
    }
}
