package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_AUTHORITY_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_REVIEW;

import com.example.seatchoice.dto.request.CommentRequest;
import com.example.seatchoice.dto.response.CommentResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;
	private final CommentRepository commentRepository;
	private final AlarmService alarmService;

	@Transactional
	public void create(Long memberId, CommentRequest.Create commentRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

		Review review = reviewRepository.findById(commentRequest.getReviewId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_REVIEW, HttpStatus.NOT_FOUND));

		commentRepository.save(Comment.of(review, member, commentRequest.getContent()));
		review.addCommentAmount();
		reviewRepository.save(review);

		alarmService.createAlarm(
			review.getMember().getId(), AlarmType.COMMENT, commentRequest.getContent(), review.getId(), member.getNickname());
	}

	@Transactional
	public void modify(Long memberId, Long commentId, CommentRequest.Modify commentRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT, HttpStatus.NOT_FOUND));

		if (!comment.getMember().equals(member)) {
			throw new CustomException(NOT_AUTHORITY_COMMENT, HttpStatus.FORBIDDEN);
		}

		comment.setContent(commentRequest.getContent());
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

	public List<CommentResponse> list(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_REVIEW, HttpStatus.NOT_FOUND));

		return commentRepository.findAllByReview(review).stream()
			.map(CommentResponse::from).collect(Collectors.toList());

	}

}
