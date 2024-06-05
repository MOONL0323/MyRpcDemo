package org.example.service;

import org.example.com.Blog;

public class BlogServiceImpl implements BlogService{
    @Override
    public Blog getBlogById(int id) {
        return Blog.builder().id(id).title("title").content("content").build();
    }
}
