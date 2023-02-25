package com.ecsp.demo.models.entity.validation;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "valid_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestValidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trace;
    private String code;

    @Builder
    public TestValidEntity(String trace, String code) {
        this.trace = trace;
        this.code = code;
    }
}
