/**
 * 
 */
package com.mtdhb.api.dao;

import java.sql.Timestamp;

import org.springframework.data.repository.CrudRepository;

import com.mtdhb.api.constant.Purpose;
import com.mtdhb.api.constant.VerificationType;
import com.mtdhb.api.entity.Verification;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/08
 */
public interface VerificationRepository extends CrudRepository<Verification, Long> {

    Verification findByCodeAndTypeAndPurposeAndUsedAndGmtCreateGreaterThan(String code, VerificationType type,
            Purpose purpose, boolean used, Timestamp gmtCreate);
}
