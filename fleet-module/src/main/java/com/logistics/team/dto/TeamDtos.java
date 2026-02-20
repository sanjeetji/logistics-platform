package com.logistics.team.dto;

import lombok.Builder;
import lombok.Data;

public class TeamDtos {

    @Data
    @Builder
    public static class CreateRegionRequest {
        private String name;
        private String description;
    }

    @Data
    @Builder
    public static class CreateHubRequest {
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String regionId;
    }

    @Data
    @Builder
    public static class CreateTeamRequest {
        private String name;
        private String hubId;
    }

    @Data
    @Builder
    public static class AssignMemberRequest {
        private String userId;
        private String role;
        private String teamId;
    }
}
