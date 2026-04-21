package com.projectpulse.dto.request;

import java.util.List;

public class AssignUsersRequest {
    private Long teamId;
    private List<Long> userIds;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}
