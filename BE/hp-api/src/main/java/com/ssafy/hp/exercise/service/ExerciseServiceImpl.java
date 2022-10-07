package com.ssafy.hp.exercise.service;

import com.ssafy.hp.*;
import com.ssafy.hp.common.type.*;
import com.ssafy.hp.exercise.*;
import com.ssafy.hp.exercise.domain.*;
import com.ssafy.hp.exercise.query.*;
import com.ssafy.hp.exercise.response.*;
import com.ssafy.hp.user.*;
import com.ssafy.hp.user.domain.*;
import com.ssafy.hp.user.service.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

import static com.ssafy.hp.NotFoundException.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseServiceImpl implements ExerciseService {

    private static final int CMD_DOING = 1;
    private static final int CMD_LIKE = 2;
    private static final int CMD_BOOKMARK = 3;


    private final ExerciseRepository exerciseRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExercisePartCategoryRepository exercisePartCategoryRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseQueryRepository exerciseQueryRepository;
    private final UserService userService;

    // 운동 종류별 조회
    @Override
    public Page<ExerciseListResponse> findByExerciseCategory(User user, Integer exerciseCategoryId, Pageable pageable) {
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseCategoryId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.CATEGORY_NOT_FOUND));
        Page<Exercise> exercises = exerciseRepository.findByExerciseCategoryOrderByExerciseNameAsc(exerciseCategory, pageable);

        return exercises.map(exercise -> ExerciseListResponse.from(
                exercise,
                userService.findByExercise(user, exercise.getExerciseId())));
    }

    // 운동 부위별 조회
    @Override
    public Page<ExerciseListResponse> findByExercisePart(User user, Integer part, Pageable pageable) {
        ExercisePartCategory exercisePartCategory = exercisePartCategoryRepository.findById(part)
                .orElseThrow(() -> new NotFoundException(NotFoundException.CATEGORY_NOT_FOUND));

        return exerciseQueryRepository.findExerciseByExercisePartCategory(exercisePartCategory, pageable)
                .map(exercise -> ExerciseListResponse.from(
                        exercise,
                        userService.findByExercise(user, exercise.getExerciseId())));
    }

    // 운동 상세정보 조회
    @Override
    public ExerciseDetailResponse findByExerciseId(User user, Integer exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.EXERCISE_NOT_FOUND));

        return ExerciseDetailResponse.from(
                exercise,
                userService.findByExercise(user, exercise.getExerciseId()),
                userExerciseRepository.countByExerciseAndUserExerciseDoing(exercise, YN.Y));
    }

    @Override
    @Transactional
    public void updateUserExerciseByUserAndExercise(User user, Integer exerciseId, YN yn, int cmd) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException(EXERCISE_NOT_FOUND));

        Optional<UserExercise> userExercise = userExerciseRepository.findByUsersAndExercise(user, exercise);

        if (userExercise.isPresent()) {
            // 이미 컬럼이 있으면 해당 컬럼을 업데이트 해주면 됨
            updateUserExerciseByCmd(userExercise.get(), yn, cmd);
        } else {
            // 처음 등록되는 컬럼이라면 컬럼을 추가한다
            UserExercise newUserExercise = UserExercise.createUserExercise(user, exercise);
            updateUserExerciseByCmd(newUserExercise, yn, cmd);
            userExerciseRepository.save(newUserExercise);
        }
    }

    @Override
    public List<ExerciseCategoryResponse> findAllExerciseCategory() {
        return exerciseCategoryRepository.findAll()
                .stream().map(ExerciseCategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExercisePartCategoryResponse> findAllExercisePartCategory() {
        return exercisePartCategoryRepository.findAll()
                .stream().map(ExercisePartCategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<List<ExerciseCalendarResponse>> findExerciseByUserExercise(User user, String search) {
        return exerciseQueryRepository.findExerciseByUserExercise(user, search);
    }

    private void updateUserExerciseByCmd(UserExercise userExercise, YN yn, int cmd) {
        if (cmd == CMD_DOING) {
            userExercise.updateUserExerciseDoing(yn);
        } else if (cmd == CMD_LIKE) {
            userExercise.updateUserExerciseLike(yn);
        } else if (cmd == CMD_BOOKMARK) {
            userExercise.updateUserExerciseBookmark(yn);
        } else {
            throw new InvalidException(InvalidException.INVALID_REQUEST);
        }
    }
}
