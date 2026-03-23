package com.bot.akane.model.entity;

import lombok.Data;

@Data
public class UserProfileEntity {
    private String userId;
    private String profileKey;
    private String profileValue;
    private Boolean isDeleted;
}
