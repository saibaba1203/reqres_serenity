package com.reqres.usersinfo;

import com.reqres.constants.EndPoints;
import com.reqres.model.UsersPojo;
import com.reqres.testbase.TestBase;
import com.reqres.utils.TestUtils;
import io.restassured.http.ContentType;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Title;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.hamcrest.Matchers.hasValue;

@RunWith(SerenityRunner.class)
public class UserCRUDTest extends TestBase {
    static String email = TestUtils.getRandomValue() + "xyz@gmail.com";
    static String first_name = "PrimUser" + TestUtils.getRandomValue();
    static String last_name = "PrimeUser" + TestUtils.getRandomValue();
    static String avatar = "Api Testing";
    static int id;


    @Title("This will create a new User")
    @Test
    public void test001() {
        UsersPojo usersPojo = new UsersPojo();
        usersPojo.setFirst_name(first_name);
        usersPojo.setLast_name(last_name);
        usersPojo.setEmail(email);
        usersPojo.setAvatar(avatar);
        SerenityRest.given().log().all()
                .contentType(ContentType.JSON)
                .body(usersPojo)
                .when()
                .post(EndPoints.CREATE_USERS)
                .then().log().all().statusCode(201);

    }

    @Title("Verify if the User was added to the application")
    @Test
    public void test002() {
        String p1 = "data.findAll{it.first_name == '";
        String p2 = "'}.get(0)";

        HashMap<String,Object> userMap = SerenityRest.given().log().all()
                .when()
                .get(EndPoints.GET_ALL_USERS)
                .then()
                .statusCode(200)
                .extract()
                .path(p1+first_name+p2);
        Assert.assertThat(userMap, hasValue(first_name));
        id = (int) userMap.get("id");
        System.out.println(id);

    }

    @Title("Update the user information and verify the updated information")
    @Test
    public void test003() {

        first_name = first_name + "_updated";

        UsersPojo usersPojo = new UsersPojo();
        usersPojo.setFirst_name(first_name);
        usersPojo.setLast_name(last_name);
        usersPojo.setEmail(email);
        usersPojo.setAvatar(avatar);
        SerenityRest.given().log().all()
                .header("Content-Type", "application/json")
                .pathParam("id", id)
                .body(usersPojo)
                .when()
                .put(EndPoints.UPDATE_USERS)
                .then().log().all().statusCode(200);

        String p1 = "data.findAll{it.firstName=='";
        String p2 = "'}.get(0)";

        HashMap<String, Object> userMap = SerenityRest.given().log().all()
                .when()
                .get(EndPoints.GET_ALL_USERS)
                .then()
                .statusCode(200)
                .extract()
                .path(p1 + first_name + p2);
        Assert.assertThat(userMap, hasValue(first_name));
    }


    @Title("Delete the User and verify if the student is deleted")
    @Test
    public void test004() {
        SerenityRest.given().log().all()
                .pathParam("id", id)
                .when()
                .delete(EndPoints.DELETE_USER)
                .then().statusCode(204);

        SerenityRest.given().log().all()
                .pathParam("id", id)
                .get(EndPoints.DELETE_USER)
                .then()
                .statusCode(404);
    }
}
