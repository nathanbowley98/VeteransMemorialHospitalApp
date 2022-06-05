package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReviewUserTest {

    @Test
    public void addFirstReview() {
        User user = new User();
        user.addReview(3);
        assertTrue(user.getAverage_rating() == 3);
    }

    @Test
    public void addTwoReviews() {
        User user = new User();
        user.addReview(3);
        user.addReview(5);
        assertTrue(user.getAverage_rating() == 4);
    }

    @Test
    public void addInvalidReviewHigh() {
        User user = new User();
        user.addReview(3);
        user.addReview(12);
        assertTrue(user.getAverage_rating() == 3);
    }

    @Test
    public void addInvalidReviewLow() {
        User user = new User();
        user.addReview(3);
        user.addReview(0);
        assertTrue(user.getAverage_rating() == 3);
    }



}
