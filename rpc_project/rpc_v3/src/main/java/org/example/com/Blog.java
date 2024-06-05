package org.example.com;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Blog implements Serializable {
    private int id;
    private String title;
    private String content;
}
