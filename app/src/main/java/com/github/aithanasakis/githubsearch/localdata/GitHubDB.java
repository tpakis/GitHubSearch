package com.github.aithanasakis.githubsearch.localdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.github.aithanasakis.githubsearch.model.GitRepository;

@Database(entities = {GitRepository.class}, version = GitHubDB.VERSION)
public abstract class GitHubDB extends RoomDatabase {

    static final int VERSION = 1;
    public static final String DB_NAME = "app_db";

    public abstract GitHubDAO getGitHubDao();

}