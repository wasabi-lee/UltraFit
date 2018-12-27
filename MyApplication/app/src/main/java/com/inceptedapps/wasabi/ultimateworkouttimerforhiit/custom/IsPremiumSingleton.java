package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom;

/**
 * Created by Wasabi on 5/11/2016.
 */
public class IsPremiumSingleton {
    private static boolean isPremium;
    private static IsPremiumSingleton instance;

    private IsPremiumSingleton(){
    }

    public static IsPremiumSingleton getInstance(){
        if (instance == null){
            instance = new IsPremiumSingleton();
        } return instance;
    }

    public void setPremium(boolean premium){
        isPremium = premium;
    }

    public boolean returnPremium(){
        return isPremium;
    }
}
