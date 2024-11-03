package com.efedorchenko.timely.model;

import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.Fine;
import com.efedorchenko.timely.entity.UserData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class UserDataDeserializer extends JsonDeserializer<UserData> {

    @Override
    public UserData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        if (node.has("workDuration")) {
            return mapper.treeToValue(node, Event.class);
        } else if (node.has("amount")) {
            return mapper.treeToValue(node, Fine.class);
        } else {
            throw new IOException("Could not determine the type of UserData: missing distinctive fields");
        }
    }
}
