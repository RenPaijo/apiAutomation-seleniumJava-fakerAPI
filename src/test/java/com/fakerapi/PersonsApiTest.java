package com.fakerapi;

import com.fakerapi.controller.PersonController;
import com.fakerapi.model.Person;
import com.fakerapi.view.PersonView;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("FakerAPI")
@Feature("Persons endpoint")
public class PersonsApiTest {

    private final PersonController personController = new PersonController();
    private final PersonView personView = new PersonView();

    @Test
    @Story("Filter persons by gender and birthday range")
    @Description("GET /api/v2/persons with _quantity=10, _gender=male, "
            + "_birthday_start=1990-01-01, _birthday_end=2000-12-31. "
            + "Verify total is 10, every gender is male, and every birthday is within range.")
    @Severity(SeverityLevel.CRITICAL)
    void personsFilteredByGenderAndBirthdayRange() {
        int quantity = 10;
        String gender = "male";
        LocalDate start = LocalDate.parse("1990-01-01");
        LocalDate end = LocalDate.parse("2000-12-31");

        List<Person> persons = personController.getPersons(quantity, gender, start.toString(), end.toString());

        String summary = personView.format(persons);
        System.out.println(summary);
        Allure.addAttachment("Persons response", "text/plain", summary);

        assertEquals(quantity, persons.size(), "Total persons should be 10");

        for (Person person : persons) {
            assertEquals(gender, person.getGender(),
                    () -> "Gender should be male for " + person.getFirstname() + " " + person.getLastname());

            LocalDate birthday = LocalDate.parse(person.getBirthday());
            assertTrue(!birthday.isBefore(start) && !birthday.isAfter(end),
                    () -> "Birthday " + birthday + " should be between " + start + " and " + end);
        }
    }
}
