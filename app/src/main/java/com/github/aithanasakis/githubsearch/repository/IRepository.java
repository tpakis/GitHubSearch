package com.github.aithanasakis.githubsearch.repository;

public interface IRepository {
   void fetchData(String query, boolean online);
   void fetchSubscribers(String url);
}
