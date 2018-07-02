/**
 * 
 */
package com.mtdhb.api.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mtdhb.api.constant.e.Purpose;
import com.mtdhb.api.constant.e.VerificationType;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/07
 */
@Data
@Entity
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String object;
    private String code;
    @Enumerated
    private VerificationType type;
    @Enumerated
    private Purpose purpose;
    @Column(name = "is_used")
    private Boolean used;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

}
