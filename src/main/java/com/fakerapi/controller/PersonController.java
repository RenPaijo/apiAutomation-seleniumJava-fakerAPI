package com.fakerapi.controller;

import com.fakerapi.config.ApiConfig;
import com.fakerapi.model.Person;
import com.fakerapi.model.PersonsResponse;
import io.restassured.response.Response;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PersonController {

    private final String baseUri;

    public PersonController() {
        this(ApiConfig.BASE_URI);
    }

    public PersonController(String baseUri) {
        this.baseUri = baseUri;
    }

    public List<Person> getPersons(int quantity, String gender, String birthdayStart, String birthdayEnd) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            Response response = given()
                    .baseUri(baseUri)
                    .queryParam("_quantity", quantity)
                    .queryParam("_gender", gender)
                    .queryParam("_birthday_start", birthdayStart)
                    .queryParam("_birthday_end", birthdayEnd)
                    .when()
                    .get(ApiConfig.PERSONS_PATH);

            if (response.statusCode() == 502) {
                continue;
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException("FakerAPI returned HTTP " + response.statusCode());
            }
            PersonsResponse personsResponse = response.as(PersonsResponse.class);
            return personsResponse.getData();
        }
        throw new BadGatewayException("FakerAPI returned HTTP 502 after 3 attempts");
    }

    public static class BadGatewayException extends RuntimeException {
        public BadGatewayException(String message) {
            super(message);
        }
    }
}
