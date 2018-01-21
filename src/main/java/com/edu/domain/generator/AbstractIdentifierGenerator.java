package com.edu.domain.generator;

import org.hibernate.id.IdentifierGenerator;

public abstract class AbstractIdentifierGenerator implements IdentifierGenerator {

    protected int digitsOf(Long value) {
        return String.valueOf(value).length();
    }
}
