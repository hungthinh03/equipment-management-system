package com.example.request.aspect;


import com.example.request.annotation.RequireRole;
import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class ValidateRoleAspect {
    @Before("@annotation(requireRole)")
    public void validateRole(JoinPoint joinPoint, RequireRole requireRole) {
        List<String> allowed = Arrays.asList(requireRole.value()); // Get roles from annotation

        String roleHeader = Arrays.stream(joinPoint.getArgs())
                .filter(String.class::isInstance) // Get all arguments then filter by string
                .map(String.class::cast)
                .filter(s -> s.equalsIgnoreCase("ADMIN")
                        || s.equalsIgnoreCase("IT")
                        || s.equalsIgnoreCase("EMPLOYEE"))
                .findFirst()
                .orElse(null);

        // Validate role
        if (roleHeader == null || !allowed.contains(roleHeader.toUpperCase())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
