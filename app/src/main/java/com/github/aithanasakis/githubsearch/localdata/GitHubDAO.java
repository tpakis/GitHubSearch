package com.github.aithanasakis.githubsearch.localdata;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.aithanasakis.githubsearch.model.GitRepository;

import java.util.List;

@Dao
public interface GitHubDAO {

    //it will return -1 if the item was already in db and not inserted
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertEntry(GitRepository entry);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int update(GitRepository entry);

    @Query("SELECT * FROM GitRepository WHERE id = :id")
    GitRepository getSpecifigEntryById(int id);

    // Removes an entry from the database
    @Delete
    void delete(GitRepository entry);

    // Gets all entries in the database
    @Query("SELECT * FROM GitRepository")
    List<GitRepository> getAllEntries();

    // Delets all entries in the database
    @Query("DELETE FROM GitRepository")
    void deleteAllEntries();
}
