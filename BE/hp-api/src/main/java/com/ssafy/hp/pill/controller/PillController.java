package com.ssafy.hp.pill.controller;

import com.ssafy.hp.config.LoginUser;
import com.ssafy.hp.pill.request.PillCheckRequest;
import com.ssafy.hp.pill.request.PillReviewRequest;
import com.ssafy.hp.pill.request.SearchRequest;
import com.ssafy.hp.pill.response.*;
import com.ssafy.hp.pill.service.PillService;
import com.ssafy.hp.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RequestMapping("/api/pills")
@RestController
@RequiredArgsConstructor
public class PillController {

    private static final int CMD_TAKING = 1;
    private static final int CMD_BOOKMARK = 2;


    private final PillService pillService;

    // 영양제 검색
    @GetMapping("/search")
    public ResponseEntity<Page<PillListResponse>> search(@PageableDefault Pageable page, @Valid SearchRequest request) {
        Page<PillListResponse> body = pillService.findBySearchFilter(request, page);
        return ResponseEntity.ok().body(body);
    }

    // 영양제 디테일 조회
    @GetMapping("/{pill_id}")
    public ResponseEntity<PillDetailResponse> findByPillId(@LoginUser User user, @PathVariable("pill_id") int pillId) {
        PillDetailResponse body = pillService.findByPillId(user, pillId);
        return ResponseEntity.ok().body(body);
    }

    // 리뷰 작성
    @PostMapping("/{pill_id}/review")
    public ResponseEntity<Void> createReview(@LoginUser User user, @PathVariable("pill_id") int pillId, @RequestBody @Valid PillReviewRequest request) {
        pillService.createReview(user, pillId, request);
        return ResponseEntity.ok().build();
    }

    // 리뷰 목록 조회
    @GetMapping("/{pill_id}/review")
    public ResponseEntity<Page<PillReviewListResponse>> findReviewByPillId(@LoginUser User user, @PageableDefault(size = 15) Pageable pageable, @PathVariable("pill_id") int pillId) {
        Page<PillReviewListResponse> body = pillService.findReviewByPillId(user, pillId, pageable);
        return ResponseEntity.ok().body(body);
    }

    // 리뷰 수정
    @PutMapping("/review/{review_id}")
    public ResponseEntity<Void> updateReview(@LoginUser User user, @PathVariable("review_id") int reviewId, @RequestBody @Valid PillReviewRequest request) {
        pillService.updateReview(user, reviewId, request);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/review/{review_id}")
    public ResponseEntity<Void> deleteReview(@LoginUser User user, @PathVariable("review_id") int reviewId) {
        pillService.deleteReview(user, reviewId);
        return ResponseEntity.ok().build();
    }

    // 생리활성 기능 목록 조회
    @GetMapping("/functionalities")
    public ResponseEntity<List<FunctionalityListResponse>> findAllByOrderByFunctionalityContentAsc() {
        List<FunctionalityListResponse> body = pillService.findAllByOrderByFunctionalityContentAsc();
        return ResponseEntity.ok().body(body);
    }

    // 기능성원료(영양소) 목록 조회
    @GetMapping("/nutrients")
    public ResponseEntity<List<NutrientListResponse>> findAllByOrderByNutrientNameAsc() {
        List<NutrientListResponse> body = pillService.findAllByOrderByNutrientNameAsc();
        return ResponseEntity.ok().body(body);
    }

    // 이미지 검색
    @PostMapping("/vision")
    public ResponseEntity<String> findKeyWordByImg(@RequestBody @Valid String data) {
        String body = pillService.findKeyWordByImg(data);
        return ResponseEntity.ok().body(body);
    }

    // 복용중 상태 변경
    @PostMapping("/taking")
    public ResponseEntity<Void> updateUserPillTakingByPill(@LoginUser User user, @RequestBody @Valid PillCheckRequest request) {
        pillService.updateUserPillByUserAndPill(user, request.getPillId(), request.getCheck(), CMD_TAKING);
        return ResponseEntity.ok().build();
    }

    // 영양제 북마크 상태 변경
    @PostMapping("/bookmark")
    public ResponseEntity<Void> updateUserPillBookmarkByPill(@LoginUser User user, @RequestBody @Valid PillCheckRequest request) {
        pillService.updateUserPillByUserAndPill(user, request.getPillId(), request.getCheck(), CMD_BOOKMARK);
        return ResponseEntity.ok().build();
    }

    // 검색어 미리보기 10개
    @GetMapping("/search/preview/{keyword}")
    public ResponseEntity<List<String>> findTop10PillNameByPillNameContainingOrderByPillNameAsc(@PathVariable("keyword") String keyword) {
        List<String> body = pillService.findTop10PillNameByPillNameContainingOrderByPillNameAsc(keyword);
        return ResponseEntity.ok().body(body);
    }

    // 영양제 일정 등록 시 자동완성 리스트
    @GetMapping("/calendar-list")
    public ResponseEntity<List<List<PillCalendarResponse>>> findPillByUserPill(@LoginUser User user, @RequestParam String search) {
        List<List<PillCalendarResponse>> body = pillService.findPillByUserPill(user, search);
        return ResponseEntity.ok().body(body);
    }


    // 해당 영양제에 대한 나의 리뷰
    @GetMapping("/{pill_id}/review/my")
    public ResponseEntity<Optional<PillReviewResponse>> findPillReviewByUser(@LoginUser User user, @PathVariable("pill_id") int pillId) {
        Optional<PillReviewResponse> body = pillService.findPillReviewByUser(user, pillId);
        return ResponseEntity.ok().body(body);
    }

    // 유사어 검색
    @GetMapping("/search/similar/{keyword}")
    public ResponseEntity<String> findSimilarNameByKeyword(@PathVariable("keyword") String keyword) {
        String body = pillService.findSimilarNameByKeyword(keyword);
        return ResponseEntity.ok().body(body);
    }
}
