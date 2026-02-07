package com.logistics.team.service;

import com.logistics.team.dto.TeamDtos;
import com.logistics.team.entity.*;
import com.logistics.team.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final RegionRepository regionRepository;
    private final HubRepository hubRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public Region createRegion(TeamDtos.CreateRegionRequest request) {
        Region region = Region.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return regionRepository.save(region);
    }

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Transactional
    public Hub createHub(TeamDtos.CreateHubRequest request) {
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new RuntimeException("Region not found"));

        Hub hub = Hub.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .region(region)
                .build();
        return hubRepository.save(hub);
    }

    public List<Hub> getHubsByRegion(String regionId) {
        return hubRepository.findByRegionId(regionId);
    }

    @Transactional
    public Team createTeam(TeamDtos.CreateTeamRequest request) {
        Hub hub = hubRepository.findById(request.getHubId())
                .orElseThrow(() -> new RuntimeException("Hub not found"));

        Team team = Team.builder()
                .name(request.getName())
                .hub(hub)
                .build();
        return teamRepository.save(team);
    }

    public List<Team> getTeamsByHub(String hubId) {
        return teamRepository.findByHubId(hubId);
    }

    @Transactional
    public TeamMember assignMember(TeamDtos.AssignMemberRequest request) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        TeamMember member = TeamMember.builder()
                .userId(request.getUserId())
                .role(request.getRole())
                .team(team)
                .build();
        return teamMemberRepository.save(member);
    }
}
