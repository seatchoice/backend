package com.example.seatchoice.repository;

import static com.example.seatchoice.entity.QAlarm.alarm;
import static com.example.seatchoice.entity.QReview.review;

import com.example.seatchoice.dto.response.AlarmResponse;
import com.example.seatchoice.dto.response.ReviewInfoResponse;
import com.example.seatchoice.entity.Alarm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Slice<AlarmResponse> alarmListBySlice(Long lastAlarmId, Long memberId, Pageable pageable) {
        List<Alarm> alarms = queryFactory
            .selectFrom(alarm)
            .where(
                ltAlarmId(lastAlarmId), // review.id < lastReviewId
                alarm.member.id.eq(memberId)
            )
            .orderBy(alarm.id.desc()) // 최신순으로 보여줌
            .limit(pageable.getPageSize() + 1) // limit보다 한 개 더 들고온다.
            .fetch();

        List<AlarmResponse> alarmResponses = AlarmResponse.of(alarms);
        if (alarmResponses == null) {
            return null;
        }

        return checkLastPage(pageable, alarmResponses);
    }

    private BooleanExpression ltAlarmId(Long alarmId) {
        if (alarmId == null) { // 요청이 처음일 때 where 절에 null을 주면 page size만큼 반환
            return null;
        }
        return alarm.id.lt(alarmId);
    }

    private Slice<AlarmResponse> checkLastPage(Pageable pageable, List<AlarmResponse> results) {
        boolean hasNext = false;
        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
