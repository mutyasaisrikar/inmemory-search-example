package com.srikar.inmemorysearchexample.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class State {

    private final String shortForm;
    private final String state;

    public static State state(String shortForm, String state) {
        return new State(shortForm, state);
    }
}
