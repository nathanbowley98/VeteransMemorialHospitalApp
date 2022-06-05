package com.example.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import android.text.TextUtils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RegisterUserTest {

    @AfterClass
    public static void tearDown() {
        System.gc();
    }

    @Test
    public void validFirstName() {
        assertTrue(RegisterUser.checkFirstName("George"));
    }

    @Test
    public void invalidFirstName() {
        assertFalse(RegisterUser.checkFirstName("GMoney$$$"));
    }

    @Test
    public void validLastName() {
        assertTrue(RegisterUser.checkFirstName("Smith"));
    }

    @Test
    public void validEmail() {
        assertTrue(RegisterUser.checkEmail("george.smith@dal.ca"));
    }

    @Test
    public void invalidEmail() {
        assertFalse(RegisterUser.checkEmail("notAmEmail"));
    }

    @Test
    public void validPassword(){
        assertTrue(RegisterUser.checkPassword("1234"));
    }

    @Test
    public void invalidPassword(){
        assertFalse(RegisterUser.checkPassword("f g"));
    }
}
