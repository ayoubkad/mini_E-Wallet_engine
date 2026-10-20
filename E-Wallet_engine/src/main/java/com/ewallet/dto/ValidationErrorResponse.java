package com.ewallet.dto;

import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String code,
        Map<String, String> errors
) {}
