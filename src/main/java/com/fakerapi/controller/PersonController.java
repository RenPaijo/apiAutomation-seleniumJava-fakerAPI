package com.fakerapi.controller;

import com.fakerapi.config.ApiConfig;
import com.fakerapi.model.Person;
import com.fakerapi.model.PersonsResponse;
import io.restassured.RestAssured;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PersonController {

    static {
        RestAssured.baseURI = ApiConfig.BASE_URI;
    }

    public List<Person> getPersons(int quantity, String gender, String birthdayStart, String birthdayEnd) {
        PersonsResponse response = given()
                .queryParam("_quantity", quantity)
                .queryParam("_gender", gender)
                .queryParam("_birthday_start", birthdayStart)
                .queryParam("_birthday_end", birthdayEnd)
                .when()
                .get(ApiConfig.PERSONS_PATH)
                .as(PersonsResponse.class);

        return response.getData();
    }
}
