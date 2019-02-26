package io.yosemite.services;

import io.yosemite.util.tuples.Triplet;

/**
 * Represents the specifier with its contract name(value0), action name(value1), and the json-formatted action data(value2).
 */
public final class ActionSpecifier extends Triplet<String, String, String> {
    public ActionSpecifier(String contract, String name, String data) {
        super(contract, name, data);
    }
}
