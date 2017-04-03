package com.codingchili.core.configuration.system;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

import com.codingchili.core.configuration.*;

import static com.codingchili.core.configuration.CoreStrings.getNoSuchValidator;
import static com.codingchili.core.security.RegexAction.*;

/**
 * @author Robin Duda
 *         <p>
 *         Contains a set of of named validators.
 */
public class ValidatorSettings extends BaseConfigurable {
    private Map<String, ParserSettings> validators = getDefaultValidators();

    public ValidatorSettings() {
        path = CoreStrings.PATH_VALIDATOR;
    }

    /**
     * @return all validators that are configured.
     */
    public Map<String, ParserSettings> getValidators() {
        return validators;
    }

    @JsonIgnore
    public ParserSettings getValidator(String name) {
        if (validators.containsKey(name)) {
            return validators.get(name);
        } else {
            throw new IllegalArgumentException(getNoSuchValidator(name));
        }
    }

    /**
     * @param validators sets the validator name:validator mappings.
     */
    public void setValidators(Map<String, ParserSettings> validators) {
        this.validators = validators;
    }

    /**
     * Adds a validator.
     *
     * @param name     the name of the parser settings to add
     * @param settings the settings for the validation to apply.
     */
    public void add(String name, ParserSettings settings) {
        validators.put(name, settings);
    }

    private Map<String, ParserSettings> getDefaultValidators() {
        Map<String, ParserSettings> validators = new HashMap<>();

        validators.put("display-name", new ParserSettings()
                .addKey("username")
                .addKey("name")
                .length(4, 32)
                .addRegex(new RegexComponent()
                        .setAction(REJECT)
                        .setLine("[A-Z,a-z,0-9]*"))
        );


        validators.put("chat-messages", new ParserSettings()
                .addKey("message")
                .length(1, 76)
                .addRegex(new RegexComponent()
                        .setAction(REPLACE)
                        .setLine("(f..(c|k))")
                        .setReplacement("*^$#!?"))
        );

        return validators;
    }
}
