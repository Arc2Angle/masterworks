package com.masterworks.masterworks.client.renderlet;

import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.data.Construct;
import java.util.Map;

public interface Renderlet {
    Submitter build(Map<Construct.Component.Key, Construct.Component> components);
}
