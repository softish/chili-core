package com.codingchili.realmregistry.model;

import com.codingchili.core.configuration.Attributes;
import com.codingchili.realmregistry.configuration.RegisteredRealm;

/**
 * @author Robin Duda
 * Contains realm metadata used in the realm-list.
 */
public class RealmMetaData extends Attributes {
    private long updated;
    private String name;
    private String description;
    private String remote;
    private String resources;
    private String version;
    private int size;
    private String type;
    private String lifetime;
    private int players = 0;
    private Boolean trusted;
    private Boolean secure;

    public RealmMetaData() {
    }

    public RealmMetaData(RegisteredRealm settings) {

        this.setName(settings.getName())
                .setDescription(settings.getDescription())
                .setRemote(settings.getRemote())
                .setResources(settings.getResources())
                .setVersion(settings.getVersion())
                .setSize(settings.getSize())
                .setType(settings.getType())
                .setLifetime(settings.getLifetime())
                .setPlayers(settings.getPlayers())
                .setTrusted(settings.getTrusted())
                .setUpdated(settings.getUpdated())
                .setSecure(settings.getSecure());
    }

    public long getUpdated() {
        return updated;
    }

    private RealmMetaData setUpdated(long updated) {
        this.updated = updated;
        return this;
    }

    public Boolean getSecure() {
        return secure;
    }

    private void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getName() {
        return name;
    }

    private RealmMetaData setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    private RealmMetaData setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getRemote() {
        return remote;
    }

    private RealmMetaData setRemote(String remote) {
        this.remote = remote;
        return this;
    }

    public String getResources() {
        return resources;
    }

    private RealmMetaData setResources(String resources) {
        this.resources = resources;
        return this;
    }

    public String getVersion() {
        return version;
    }

    private RealmMetaData setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getSize() {
        return size;
    }

    private RealmMetaData setSize(int size) {
        this.size = size;
        return this;
    }

    public String getType() {
        return type;
    }

    private RealmMetaData setType(String type) {
        this.type = type;
        return this;
    }

    public String getLifetime() {
        return lifetime;
    }

    private RealmMetaData setLifetime(String lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public int getPlayers() {
        return players;
    }

    private RealmMetaData setPlayers(int players) {
        this.players = players;
        return this;
    }

    public Boolean getTrusted() {
        return trusted;
    }

    private RealmMetaData setTrusted(Boolean trusted) {
        this.trusted = trusted;
        return this;
    }
}
