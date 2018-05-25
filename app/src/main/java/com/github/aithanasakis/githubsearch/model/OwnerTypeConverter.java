package com.github.aithanasakis.githubsearch.model;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.List;

public class OwnerTypeConverter {

    @TypeConverter
    public static GitOwner toOwner(String ownerJson){
        
        if (ownerJson == null) {
            return (null);
        }
        Type type = new TypeToken<GitOwner>() {}.getType();
        Gson gson = new Gson();
        GitOwner owner = gson.fromJson(ownerJson, type);
        return owner;
    }

    @TypeConverter
    public static String toString(GitOwner owner){
        if (owner == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<GitOwner>() {}.getType();
        String json = gson.toJson(owner, type);
        return json;
    }
}
