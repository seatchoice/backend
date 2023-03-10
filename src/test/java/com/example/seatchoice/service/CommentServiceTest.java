package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_AUTHORITY_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_REVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.request.CommentRequest;
import com.example.seatchoice.dto.request.CommentRequest.Create;
import com.example.seatchoice.dto.request.CommentRequest.Modify;
import com.example.seatchoice.dto.response.CommentResponse;
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
	@DisplayName("?????? ?????? ??????")
	void createSuccess() {
		// given
		CommentRequest.Create req = new Create(2L, "??????");
		Member member = Member.builder().build();
		member.setId(1L);
		Member member1 = Member.builder().build();
		member1.setId(2L);
		Review review = Review.builder().commentAmount(1L).build();
		review.setId(2L);
		review.setMember(member1);

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.of(review));
		ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

		// when
		commentService.create(1L, req);

		// then
		verify(commentRepository, times(1)).save(captor.capture());
		assertEquals(review.getCommentAmount(), 2L);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void createFail_notFoundMember() {
		// given
		CommentRequest.Create req = new Create(2L, "??????");

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.create(1L, req));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void createFail_notFoundReview() {
		// given
		CommentRequest.Create req = new Create(2L, "??????");
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.create(1L, req));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("?????? ?????? ??????")
	void modifySuccess() {
		// given
		CommentRequest.Modify req = new Modify("?????? ??????");
		Member member = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.content("??????")
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));

		// when
		commentService.modify(1L, 1L, req);

		// then
		assertEquals(comment.getContent(), "?????? ??????");
	}

	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void modifyFail_notFoundMember() {
		// given
		CommentRequest.Modify req = new Modify("?????? ??????");

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, 1L, req));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void modifyFail_notFoundReview() {
		// given
		CommentRequest.Modify req = new Modify("?????? ??????");
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(reviewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, 1L, req));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ??????????????? ????????????.")
	void modifyFail_notAuthorityComment() {
		// given
		CommentRequest.Modify req = new Modify("?????? ??????");
		Member member = Member.builder().build();
		Member member2 = Member.builder().build();
		Comment comment = Comment.builder()
			.member(member)
			.content("??????")
			.build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member2));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.of(comment));

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.modify(1L, 1L, req));

		// then
		assertEquals(exception.getErrorCode(), NOT_AUTHORITY_COMMENT);
	}

	@Test
	@DisplayName("?????? ?????? ??????")
	void deleteSuccess() {
		// given
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
		commentService.delete(1L, 1L);

		// then
		verify(commentRepository, times(1)).delete(captor.capture());
		assertEquals(0, comment.getReview().getCommentAmount());
	}

	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void deleteFail_notFoundMember() {
		// given
		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.delete(1L, 1L));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_MEMBER);
	}

	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
	void deleteFail_notFoundContent() {
		// given
		Member member = Member.builder().build();

		given(memberRepository.findById(anyLong()))
			.willReturn(Optional.of(member));
		given(commentRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> commentService.delete(1L, 1L));

		// then
		assertEquals(exception.getErrorCode(), NOT_FOUND_COMMENT);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ??????????????? ????????????.")
	void deleteFail_notAuthority() {
		// given
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
			() -> commentService.delete(1L, 1L));

		// then
		assertEquals(exception.getErrorCode(), NOT_AUTHORITY_COMMENT);

	}

	@Test
	@DisplayName("?????? ?????? ??????")
	void listSuccess() {
		// given
		Member member = Member.builder().nickname("????????????").build();
		member.setId(4L);
		Review review = Review.builder().build();
		Comment comment1 = Comment.builder()
			.content("??????1")
			.member(member)
			.build();
		comment1.setId(1L);
		comment1.setUpdatedAt(LocalDateTime.now());
		Comment comment2 = Comment.builder()
			.content("??????2")
			.member(member)
			.build();
		comment2.setId(2L);
		comment2.setUpdatedAt(LocalDateTime.now());
		Comment comment3 = Comment.builder()
			.content("??????3")
			.member(member)
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
		List<CommentResponse> result = commentService.list(1L);

		// then
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getUserId(), 4);
		assertEquals(result.get(0).getNickname(), "????????????");
		assertEquals(result.get(0).getContent(), "??????1");
		assertEquals(result.get(1).getContent(), "??????2");
		assertEquals(result.get(2).getContent(), "??????3");
	}

	@DisplayName("????????? ?????? ?????? - ?????? ?????? ??????")
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

	@DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
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