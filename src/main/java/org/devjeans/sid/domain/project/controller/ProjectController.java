package org.devjeans.sid.domain.project.controller;

import org.devjeans.sid.domain.project.dto.create.CreateProjectRequest;
import org.devjeans.sid.domain.project.dto.create.CreateProjectResponse;
import org.devjeans.sid.domain.project.dto.read.DetailProjectResponse;
import org.devjeans.sid.domain.project.dto.read.ListProjectResponse;
import org.devjeans.sid.domain.project.dto.scrap.ScrapResponse;
import org.devjeans.sid.domain.project.dto.update.UpdateProjectRequest;
import org.devjeans.sid.domain.project.dto.update.UpdateProjectResponse;
import org.devjeans.sid.domain.project.entity.Project;
import org.devjeans.sid.domain.project.service.ProjectService;
import org.devjeans.sid.domain.project.service.ScrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ProjectController {
    private final ProjectService projectService;
    private final ScrapService scrapService;
    @Autowired
    public ProjectController(ProjectService projectService, ScrapService scrapService) {
        this.projectService = projectService;
        this.scrapService = scrapService;
    }

    // project create
    @PostMapping("/api/project/create")
    public ResponseEntity<CreateProjectResponse> projectCreatePost(@RequestBody CreateProjectRequest createProjectRequest){
        Project project= projectService.projectCreate(createProjectRequest);
        CreateProjectResponse createProjectResponse = CreateProjectResponse.fromEntity(project);
        return new ResponseEntity<>(createProjectResponse, HttpStatus.OK);
    }

    // project read - list
    @GetMapping("/api/project/list")
    public ResponseEntity<Page<ListProjectResponse>> projectListGet(@PageableDefault(size=10, sort ="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ResponseEntity<>(projectService.projectReadAll(pageable),HttpStatus.OK);
    }

    // project read - detail
    @GetMapping("/api/project/{id}")
    public ResponseEntity<DetailProjectResponse> projectDetailGet(@PathVariable Long id){
        return new ResponseEntity<>(projectService.projectReadDetail(id), HttpStatus.OK) ;
    }

    // project update
    @PutMapping("/api/project/{id}/update")
    public ResponseEntity<UpdateProjectResponse> projectUpdatePut(@RequestBody UpdateProjectRequest updateProjectRequest, @PathVariable Long id){
        UpdateProjectResponse updateProjectResponse= projectService.projectUpdate(updateProjectRequest,id);
        return new ResponseEntity<>(updateProjectResponse, HttpStatus.OK);
    }

    // project delete
    @DeleteMapping("/api/project/{id}")
    public ResponseEntity<String> projectDelete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>("delete success",HttpStatus.OK);
    }

    // project scrap 생성 (redis)
    @PostMapping("api/project/{id}/scrap")
    public ResponseEntity<ScrapResponse> projectDoScrap(@PathVariable Long id) {
        ScrapResponse response = scrapService.scrapProject(id) ;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // project scrap 조회 (redis)
    @GetMapping("/api/project/{id}/scrap")
    public Long getProjectScrapCount(@PathVariable Long id){
        return scrapService.getProjectScrapCount(id);
    }

    // 로그인하고 있는 회원의 scrap 목록 조회 (redis)
    @GetMapping("/api/project/scrap")
    public ResponseEntity<Page<ListProjectResponse>> myScrap(@PageableDefault(size=5, sort ="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ResponseEntity<>(scrapService.getMemberScrapProjectList(pageable), HttpStatus.OK) ;
    }

    // project scrap 삭제 (redis)
    @DeleteMapping("/project/{id}/scrap")
    public ResponseEntity<ScrapResponse> projectDeleteScrap(@PathVariable Long id) {
        ScrapResponse response = scrapService.unscrapProject(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
