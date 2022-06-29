package com.example.rabbitexample.rabbite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TranslationJobStatusType {
    @JsonProperty("InProgress")
    IN_PROGRESS,
    @JsonProperty("Finished")
    FINISHED
}
