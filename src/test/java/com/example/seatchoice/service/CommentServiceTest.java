package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_AUTHORITY_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_REVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.cond.AlarmCond;
import com.example.seatchoice.dto.cond.CommentCond;
import com.example.seatchoice.dto.param.CommentParam;
import com.example.seatchoice.dto.param.CommentParam.Create;
import com.example.seatchoice.dto.param.CommentParam.Delete;
import com.example.seatchoice.dto.param.CommentParam.Modify;
import com.example.seatchoice.entity.Comment;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.CommentRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private AlarmService alarmService;

	@InjectMocks
	private CommentService commentService;

	@Test
	@DisplayName("댓글 작성 성공")
	void createSuccess() {
		// given
		CommentParam.Create param = new Create(1L, 2L, "댓글");
		Member member = Member.builder().build();
		member.setId(1L);
		Review review = Review.builder().commentAmount(1L).build();
		review.setId(2L);

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.of(review));
		ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
		given(alarmService.createAlarm(anyLong(), any(), anyString()))
			.willReturn(AlarmCond.builder().build());

		// when
		commentService.create(param);

		// then
		verify(commentRepository, times(1)).save(captor.capture());
		assertEquals(review.getCommentAmount(), 2L);
	}

	@Test
	@DisplayName("댓글 작성 실패 - 해당 유저 없음")
	void createFail_notFoundMember() {
		// given
		CommentParam.Create param = new Create(1L, 2L, "댓글");

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.create(param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@Test
	@DisplayName("댓글 작성 실패 - 해당 리뷰 없음")
	void createFail_notFoundReview() {
		// given
		CommentParam.Create param = new Create(1L, 2L, "댓글");
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.create(param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("댓글 수정 성공")
	void modifySuccess() {
		// given
		CommentParam.Modify param = new Modify(1L, "수정 댓글");
		Member member = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.content("댓글")
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));

		// when
		commentService.modify(1L, param);

		// then
		assertEquals(comment.getContent(), "수정 댓글");
	}

	@DisplayName("댓글 수정 실패 - 해당 유저 없음")
	void modifyFail_notFoundMember() {
		// given
		CommentParam.Modify param = new Modify(1L, "수정 댓글");

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@DisplayName("댓글 수정 실패 - 해당 리뷰 없음")
	void modifyFail_notFoundReview() {
		// given
		CommentParam.Modify param = new Modify(1L, "수정 댓글");
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("댓글 수정 실패 - 수정권한이 없습니다.")
	void modifyFail_notAuthorityComment() {
		// given
		CommentParam.Modify param = new Modify(1L, "수정 댓글");
		Member member = Member.builder().build();
		Member member2 = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.content("댓글")
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member2));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_AUTHORITY_COMMENT);
	}

	@Test
	@DisplayName("댓글 삭제 성공")
	void deleteSuccess() {
		// given
		CommentParam.Delete param = new Delete(1L);
		Member member = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.review(Review.builder().commentAmount(1L).build())
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));
		ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

		// when
		commentService.delete(1L, param);

		// then
		verify(commentRepository, times(1)).delete(captor.capture());
		assertEquals(0, comment.getReview().getCommentAmount());
	}

	@DisplayName("댓글 삭제 실패 - 해당 유저 없음")
	void deleteFail_notFoundMember() {
		// given
		CommentParam.Delete param = new Delete(1L);

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.delete(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@DisplayName("댓글 삭제 실패 - 해당 댓글 없음")
	void deleteFail_notFoundContent() {
		// given
		CommentParam.Delete param = new Delete(1L);
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.delete(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_COMMENT);
	}

	@Test
	@DisplayName("댓글 삭제 실패 - 삭제권한이 없습니다.")
	void deleteFail_notAuthority() {
		// given
		CommentParam.Delete param = new Delete(1L);
		Member member = Member.builder().build();
		Member member2 = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member2));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.delete(1L, param));

		// then
		assertEquals(exception.getErrorCode(), NOT_AUTHORITY_COMMENT);

	}

	@Test
	@DisplayName("댓글 조회 성공")
	void listSuccess() {
		// given
		Review review = Review.builder().build();
		Comment comment1 = Comment.builder()
			.content("댓글1")
			.member(Member.builder().nickname("제로펩시").build())
			.build();
		comment1.setId(1L);
		comment1.setUpdatedAt(LocalDateTime.now());
		Comment comment2 = Comment.builder()
			.content("댓글2")
			.member(Member.builder().nickname("제로펩시").build())
			.build();
		comment2.setId(2L);
		comment2.setUpdatedAt(LocalDateTime.now());
		Comment comment3 = Comment.builder()
			.content("댓글3")
			.member(Member.builder().nickname("제로펩시").build())
			.build();
		comment3.setId(3L);
		comment3.setUpdatedAt(LocalDateTime.now());
		List<Comment> commentList = List.of(
			comment1, comment2, comment3
		);

		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.of(review));
		given(commentRepository.findAllByReview(any()))
			.willReturn(commentList);

		// when
		List<CommentCond> result = commentService.list(1L);

		// then
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getNickname(), "제로펩시");
		assertEquals(result.get(0).getContent(), "댓글1");
		assertEquals(result.get(1).getContent(), "댓글2");
		assertEquals(result.get(2).getContent(), "댓글3");
	}

	@DisplayName("댓글들 조회 실패 - 해당 리뷰 없음")
	void listFail_notFoundReview() {
		// given
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.list(1L));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_REVIEW);
	}

	@DisplayName("댓글 조회 실패 - 해당 댓글 없음")
	void listFail_notFoundContent() {
		// given
		Review review = Review.builder().build();

		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.of(review));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.list(1L));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_COMMENT);
	}
}