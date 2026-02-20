package com.logistics.team.service;

import java.util.List;
import java.util.Objects;
import com.logistics.team.dto.TeamDtos;
import com.logistics.team.entity.*;
import com.logistics.team.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final RegionRepository regionRepository;
    private final HubRepository hubRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    @NonNull
    public Region createRegion(TeamDtos.CreateRegionRequest request) {
        Region region = Region.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return Objects.requireNonNull(regionRepository.save(region));
    }

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Transactional
    @NonNull
    public Hub createHub(TeamDtos.CreateHubRequest request) {
        Region region = regionRepository.findById(Objects.requireNonNull(request.getRegionId()))
                .orElseThrow(() -> new RuntimeException("Region not found"));

        Hub hub = Hub.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .region(region)
                .build();
        return Objects.requireNonNull(hubRepository.save(hub));
    }

    public List<Hub> getHubsByRegion(String regionId) {
        return hubRepository.findByRegionId(regionId);
    }

    @Transactional
    @NonNull
    public Team createTeam(TeamDtos.CreateTeamRequest request) {
        Hub hub = hubRepository.findById(Objects.requireNonNull(request.getHubId()))
                .orElseThrow(() -> new RuntimeException("Hub not found"));

        Team team = Team.builder()
                .name(request.getName())
                .hub(hub)
                .build();
        return Objects.requireNonNull(teamRepository.save(team));
    }

    public List<Team> getTeamsByHub(String hubId) {
        return teamRepository.findByHubId(hubId);
    }

    @Transactional
    @NonNull
    public TeamMember assignMember(TeamDtos.AssignMemberRequest request) {
        Team team = teamRepository.findById(Objects.requireNonNull(request.getTeamId()))
                .orElseThrow(() -> new RuntimeException("Team not found"));

        TeamMember member = TeamMember.builder()
                .userId(request.getUserId())
                .role(request.getRole())
                .team(team)
                .build();
        return Objects.requireNonNull(teamMemberRepository.save(member));
    }
}
