package pro.grape_server.domain.grape.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pro.grape_server.domain.grape.controller.dto.request.CreateRecordRequest;
import pro.grape_server.domain.grape.controller.dto.request.CreateGrapeRequest;
import pro.grape_server.domain.grape.controller.dto.request.UpdateGrapeRequest;
import pro.grape_server.domain.grape.controller.dto.request.UpdateRecordRequest;
import pro.grape_server.domain.grape.controller.dto.response.*;
import pro.grape_server.domain.grape.service.RecordService;
import pro.grape_server.domain.grape.service.GrapeService;
import pro.grape_server.domain.grape.service.dto.GrapeInfoResult;
import pro.grape_server.domain.grape.service.dto.GrapeOverviewResult;
import pro.grape_server.global.security.CustomUserDetails;
import pro.grape_server.model.entity.Record;

import java.util.List;

@RestController("rest")
@RequiredArgsConstructor
@RequestMapping("/api/grapes")
public class GrapeController {
    private final GrapeService grapeService;
    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<CreateGrapeResponse> CreateGrape(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateGrapeRequest request
    ) {
        Long grapeId = grapeService.create(userDetails.getUserId(), request.title(), request.targetCount(), request.reward());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateGrapeResponse(grapeId));
    }

    @PostMapping("/record")
    public ResponseEntity<CreateRecordResponse> CreateRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateRecordRequest request
    ) {
        Long recordId = recordService.create(userDetails.getUserId(), request.grapeId(), request.memo(), request.recordDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateRecordResponse(recordId));
    }

    @GetMapping("/{grapeId}/overview")
    public ResponseEntity<GetGrapeResponse> GetGrape(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long grapeId
    ) {
        GrapeOverviewResult result = grapeService.getOverview(userDetails.getUserId(), grapeId);
        return ResponseEntity.ok(GetGrapeResponse.from(result));
    }

    @GetMapping("/overview")
    public ResponseEntity<GetGrapeResponse> getInProgressGrape(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GrapeOverviewResult result = grapeService.getInProgressGrapeOverView(userDetails.getUserId());
        return ResponseEntity.ok(GetGrapeResponse.from(result));
    }

    @PatchMapping("/record/{recordId}")
    public ResponseEntity<Void> updateRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId,
            @RequestBody UpdateRecordRequest request
    ) {
        recordService.update(userDetails.getUserId(), recordId, request.memo(), request.recordDate());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        recordService.delete(userDetails.getUserId(), recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/record")
    public ResponseEntity<GetRecordResponse> GetRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long recordId
    ) {
        Record record = recordService.get(userDetails.getUserId(), recordId);
        return ResponseEntity.ok(GetRecordResponse.from(record));
    }

    @PatchMapping("/{grapeId}")
    public ResponseEntity<Void> updateGrape(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long grapeId,
            @RequestBody UpdateGrapeRequest request
    ) {
        grapeService.update(userDetails.getUserId(), grapeId, request.title(), request.reward(), request.targetCount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/in-progress")
    public ResponseEntity<GetInProgressGrapeResponse> getInProgressGrapeInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GrapeInfoResult result = grapeService.getInProgressGrapeInfo(userDetails.getUserId());
        return ResponseEntity.ok(GetInProgressGrapeResponse.from(result));
    }

    @GetMapping("/exists")
    public ResponseEntity<CheckGrapeExistsResponse> checkGrapeExists(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean exists = grapeService.hasGrape(userDetails.getUserId());
        return ResponseEntity.ok(new CheckGrapeExistsResponse(exists));
    }

    @GetMapping("/activityGrid")
    public ResponseEntity<GetActivityGridResponse> getActivityGrid(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int period
    ) {
        List<List<Integer>> activityGrid = grapeService.getActivityGrid(userDetails.getUserId(), period);
        return ResponseEntity.ok(new GetActivityGridResponse(activityGrid));
    }

}
