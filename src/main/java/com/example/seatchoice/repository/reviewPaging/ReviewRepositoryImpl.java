package com.example.seatchoice.repository.reviewPaging;

import static com.example.seatchoice.entity.QReview.review;

import com.example.seatchoice.dto.cond.ReviewInfoCond;
import com.example.seatchoice.entity.Review;
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
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<ReviewInfoCond> searchBySlice(Long lastReviewId, Long seatId,
		Pageable pageable) {
		List<Review> reviews = queryFactory
			.selectFrom(review)
			.where(
				ltReviewId(lastReviewId), // review.id < lastReviewId
				review.theaterSeat.id.eq(seatId)
			)
			.orderBy(review.id.desc()) // 최신순으로 보여줌
			.limit(pageable.getPageSize() + 1) // limit보다 한 개 더 들고온다.
			.fetch();

		List<ReviewInfoCond> reviewInfoConds = ReviewInfoCond.of(reviews);
		if (reviewInfoConds == null) {
			return null;
		}
		return checkLastPage(pageable, reviewInfoConds);
	}

	// 동적 쿼리를 위한 BooleanExpression
	private BooleanExpression ltReviewId(Long reviewId) {
		if (reviewId == null) { // 요청이 처음일 때 where 절에 null을 주면 page size만큼 반환
			return null;
		}
		return review.id.lt(reviewId);
	}

	private Slice<ReviewInfoCond> checkLastPage(Pageable pageable, List<ReviewInfoCond> results) {
		boolean hasNext = false;
		// 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
		if (results.size() > pageable.getPageSize()) {
			hasNext = true;
			results.remove(pageable.getPageSize());
		}

		return new SliceImpl<>(results, pageable, hasNext);
	}
}
