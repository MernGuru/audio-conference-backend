package com.brh.entities.cores.types;

import com.brh.entities.cores.types.Gender;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class GenderAdapter extends XmlAdapter<String, Gender> implements JsonbAdapter<Gender,String> {
    @Override
    public String adaptToJson(Gender gender) throws Exception {
        return marshal(gender);
    }

    @Override
    public Gender adaptFromJson(String string) throws Exception {
        return unmarshal(string);
    }

    @Override
    public Gender unmarshal(String string) throws Exception {
        return switch (string) {
            case "Masculin" -> Gender.MALE;
            case "Féminin" -> Gender.FEMALE;
            case "Non applicable" -> Gender.NOT_APPLICABLE;
            default -> null;
        };
    }

    @Override
    public String marshal(Gender gender) throws Exception {
        if (gender == null) {
            return null;
        }
        return switch (gender){
            case NOT_APPLICABLE -> "Non applicable";
            case MALE -> "Masculin";
            case FEMALE -> "Féminin";
        };
    }
}