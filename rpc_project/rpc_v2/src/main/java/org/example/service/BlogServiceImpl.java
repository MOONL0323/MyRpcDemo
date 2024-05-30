package org.example.service;

import org.example.message.Blog;

public class BlogServiceImpl implements BlogService{
    @Override
    public Blog getBlogById(String title) {
        return Blog.builder().title(title).content("content").build();
    }
}
