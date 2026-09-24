package com.fakerapi.view;

import com.fakerapi.model.Person;
import java.util.List;

public class PersonView {

    public String format(List<Person> persons) {
        StringBuilder table = new StringBuilder("Name | Gender | Birthday\n");
        for (Person person : persons) {
            table.append(person.getFirstname())
                    .append(' ')
                    .append(person.getLastname())
                    .append(" | ")
                    .append(person.getGender())
                    .append(" | ")
                    .append(person.getBirthday())
                    .append('\n');
        }
        return table.toString();
    }
}
