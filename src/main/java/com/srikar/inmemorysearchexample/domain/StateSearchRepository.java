package com.srikar.inmemorysearchexample.domain;

import java.util.List;

public interface StateSearchRepository {

    List<String> search(String inputState);
}
