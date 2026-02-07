package com.logistics.team.controller;

import com.logistics.team.dto.TeamDtos;
import com.logistics.team.entity.*;
import com.logistics.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/regions")
    public ResponseEntity<Region> createRegion(@RequestBody TeamDtos.CreateRegionRequest request) {
        return ResponseEntity.ok(teamService.createRegion(request));
    }

    @GetMapping("/regions")
    public ResponseEntity<List<Region>> getAllRegions() {
        return ResponseEntity.ok(teamService.getAllRegions());
    }

    @PostMapping("/hubs")
    public ResponseEntity<Hub> createHub(@RequestBody TeamDtos.CreateHubRequest request) {
        return ResponseEntity.ok(teamService.createHub(request));
    }

    @GetMapping("/hubs")
    public ResponseEntity<List<Hub>> getHubsByRegion(@RequestParam String regionId) {
        return ResponseEntity.ok(teamService.getHubsByRegion(regionId));
    }

    @PostMapping("/")
    public ResponseEntity<Team> createTeam(@RequestBody TeamDtos.CreateTeamRequest request) {
        return ResponseEntity.ok(teamService.createTeam(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<Team>> getTeamsByHub(@RequestParam String hubId) {
        return ResponseEntity.ok(teamService.getTeamsByHub(hubId));
    }

    @PostMapping("/members")
    public ResponseEntity<TeamMember> assignMember(@RequestBody TeamDtos.AssignMemberRequest request) {
        return ResponseEntity.ok(teamService.assignMember(request));
    }
}
