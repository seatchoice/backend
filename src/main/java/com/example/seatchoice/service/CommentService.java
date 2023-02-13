package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_AUTHORITY_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_REVIEW;

import com.example.seatchoice.dto.cond.CommentCond;
import com.example.seatchoice.dto.param.CommentParam;
import com.example.seatchoice.entity.Comment;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.CommentRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.type.AlarmType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;
	private final CommentRepository commentRepository;
	private final AlarmService alarmService;

	@Transactional
	public void create(Long memberId, CommentParam.Create commentParam) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

		Review review = reviewRepository.findById(commentParam.getReviewId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_REVIEW, HttpStatus.NOT_FOUND));

		commentRepository.save(
			Comment.of(review, member, commentParam.getContent()));

		review.addCommentAmount();

		String commentsUrl =
			"https://43.200.67.139:8080/api/review/" + review.getId() + "/comments";

		alarmService.createAlarm(member.getId(), AlarmType.COMMENT, commentsUrl);

	}

	@Transactional
	public void modify(Long memberId, Long commentId, CommentParam.Modify commentParam) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT, HttpStatus.NOT_FOUND));

		if (!comment.getMember().equals(member)) {
			throw new CustomException(NOT_AUTHORITY_COMMENT, HttpStatus.FORBIDDEN);
		}

		comment.setContent(commentParam.getContent());
	}

	@Transactional
	public void delete(Long memberId, Long commentId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT, HttpStatus.NOT_FOUND));

		if (!comment.getMember().equals(member)) {
			throw new CustomException(NOT_AUTHORITY_COMMENT, HttpStatus.FORBIDDEN);
		}

		commentRepository.delete(comment);

		comment.getReview().minusCommentAmount();

	}

	public List<CommentCond> list(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_REVIEW, HttpStatus.NOT_FOUND));

		return commentRepository.findAllByReview(review).stream()
			.map(CommentCond::from).collect(Collectors.toList());

	}

}
