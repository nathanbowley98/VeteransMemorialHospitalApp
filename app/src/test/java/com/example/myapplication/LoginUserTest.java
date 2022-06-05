package com.example.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class LoginUserTest  {

    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("Set up before class");
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("Set up");
    }
    @Test
    public void loginUserNameIsEmpty() {
        System.out.println("loginByUserName");
        String username="";
        String msg = "";
        if (TextUtils.isEmpty(username)){
            msg = "username is empty";
        }
        Assert.assertEquals("username is empty",msg);
    }
    @Test
    public void loginPwdIsEmpty() {
        System.out.println("loginPwdIsEmpty");
        String inputPwd="";
        String msg = "";
        if (TextUtils.isEmpty(inputPwd)){
            msg = "password is empty";
        }
        Assert.assertEquals("password is empty",msg);
    }
    @Test
    public void login() {
        System.out.println("start login");
        String userName = "abv123@dal.ca";
        String password = "abc123456";
        String msg = "";
        if (TextUtils.isEmpty(userName)){
            msg = "username is empty";
        }else if (TextUtils.isEmpty(password)){
            msg = "password is empty";
        }

        Assert.assertTrue(TextUtils.isEmpty(msg));
    }
}
