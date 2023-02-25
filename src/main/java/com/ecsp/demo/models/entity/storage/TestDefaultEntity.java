package com.ecsp.demo.models.entity.storage;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "default_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestDefaultEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trace;
    private String code;

    @Builder
    public TestDefaultEntity(String trace, String code) {
        this.trace = trace;
        this.code = code;
    }
}
