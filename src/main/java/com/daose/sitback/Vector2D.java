package com.daose.sitback;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Vector2D {
    public float x, y;

    public Vector2D() {
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
