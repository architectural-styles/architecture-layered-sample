package com.architecture.layered.presentation.common.dto;

import java.time.LocalDate;

public record UserResponse(String id, String name, LocalDate birthDate) {}
