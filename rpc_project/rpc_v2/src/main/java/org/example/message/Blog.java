package org.example.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Blog implements Serializable {
    private String title;
    private String content;
    private User author;
}
