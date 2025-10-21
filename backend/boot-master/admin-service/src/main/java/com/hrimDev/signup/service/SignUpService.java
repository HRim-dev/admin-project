package org.hrimDev.signup.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface SignUpService {
    public boolean checkDuplicateByUserId(String userId) throws Exception;
}
