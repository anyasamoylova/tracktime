package com.sam.tracktime.obj;

import com.sam.tracktime.R;

import java.util.Arrays;
import java.util.List;

public class Icons {
    private static final List<Integer> icons = Arrays.asList(
            R.drawable.car,
            R.drawable.plane,
            R.drawable.bicycle,
            R.drawable.weightlifting,
            R.drawable.ball,
            R.drawable.kayak,
            R.drawable.controller,
            R.drawable.headphones,
            R.drawable.photography,
            R.drawable.mouse,
            R.drawable.protractor,
            R.drawable.atomic,
            R.drawable.compass,
            R.drawable.wrench,
            R.drawable.paintbrush,
            R.drawable.grater,
            R.drawable.drink,
            R.drawable.cocktail,
            R.drawable.smoking,
            R.drawable.cookie,
            R.drawable.shower,
            R.drawable.plant,
            R.drawable.cactus,
            R.drawable.donut,
            R.drawable.shopping,
            R.drawable.band,
            R.drawable.toothbrush,
            R.drawable.washing,
            R.drawable.home,
            R.drawable.fish,
            R.drawable.cat,
            R.drawable.duck,
            R.drawable.bones,
            R.drawable.sun);

    public Icons() {
    }

    public static List<Integer> getIcons() {
        return icons;
    }
}
