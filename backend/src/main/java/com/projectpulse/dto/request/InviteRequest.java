package com.projectpulse.dto.request;

import jakarta.validation.constraints.NotBlank;

public class InviteRequest {
    @NotBlank
    private String emails;
    private Long sectionId;
    private String customMessage;

    public String getEmails() { return emails; }
    public void setEmails(String emails) { this.emails = emails; }
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
}
