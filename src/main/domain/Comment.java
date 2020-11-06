package main.domain;

import java.time.LocalDateTime;

// 댓글 (병원 페이지에 사용)
public class Comment {
    private int commentId;
    private String hospitalId;
    private String content;
    private String writer;
    private LocalDateTime dateTime;

    public Comment(int commentId, String hospitalId, String content, String writer, LocalDateTime dateTime) {
        this.commentId = commentId;
        this.hospitalId = hospitalId;
        this.content = content;
        this.writer = writer;
        this.dateTime = dateTime;
    }
}
