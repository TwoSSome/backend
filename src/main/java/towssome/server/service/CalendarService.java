package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.advice.MemberAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.*;
import towssome.server.enumrated.PostType;
import towssome.server.exception.NotFoundCalendarException;
import towssome.server.exception.NotFoundEntityException;
import towssome.server.exception.UnauthorizedActionException;
import towssome.server.repository.*;
import towssome.server.repository.calendar_post.CalendarPostRepository;
import towssome.server.repository.calendar_post_comment.CalendarPostCommentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService implements CalendarServiceInterface {

    private final CalendarScheduleRepository calendarScheduleRepository;
    private final CalendarTagRepository calendarTagRepository;
    private final CalendarPostCommentRepository calendarPostCommentRepository;
    private final CalendarPostRepository calendarPostRepository;
    private final CalendarRepository calendarRepository;
    private final PhotoRepository photoRepository;
    private final PhotoAdvice photoAdvice;
    private final MemberAdvice memberAdvice;

    @Override
    public CalendarTag createCalendarTag(CreateCalendarTagDTO dto) {

        Calendar calendar = calendarRepository.findByAuth(dto.member()).orElseThrow(
                () -> new NotFoundCalendarException("해당 멤버의 캘린더가 없습니다")
        );
        List<CalendarTag> allTagInCalendar = calendarTagRepository.findAllByCalendar(calendar);
        if (allTagInCalendar.size() >= 8) { // 총 태그의 사이즈는 개인 태그를 제외하고 총 8개
            throw new RuntimeException("생성 가능한 태그 개수 초과");
        }

        return calendarTagRepository.save(new CalendarTag(
                dto.name(), dto.color(), calendar, false, false
        ));
    }

    @Override
    @Transactional
    public void deleteCalendarTag(long id) {
        if (!calendarTagRepository.existsById(id)) {
            throw new NotFoundEntityException("해당 태그를 찾지 못했습니다");
        }
        calendarTagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateCalendarTag(UpdateCalendarTagDTO dto) {
        CalendarTag calendarTag = calendarTagRepository.findById(dto.id()).orElseThrow(
                () -> new NotFoundCalendarException("해당 태그를 찾지 못했습니다")
        );

        calendarTag.updateTag(dto.name(), dto.color());
    }

    @Override
    public CalendarTagInfo getCalendarTag(Long id) {
        CalendarTag calendarTag = calendarTagRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 태그를 찾지 못했습니다")
        );

        return new CalendarTagInfo(
                calendarTag.getId(),
                calendarTag.getName(),
                calendarTag.getColor(),
                calendarTag.getIsDefaultTag(),
                calendarTag.getIsPersonalTag()
        );
    }

    @Override
    public List<CalendarTagInfo> getAllCalendarTagInfo(Member member) {
        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("해당 캘린더가 없습니다")
        );

        List<CalendarTag> calendarTagList = calendarTagRepository.findAllByCalendar(calendar);
        var result = new ArrayList<CalendarTagInfo>();

        for (CalendarTag calendarTag : calendarTagList) {
            result.add(new CalendarTagInfo(
                    calendarTag.getId(),
                    calendarTag.getName(),
                    calendarTag.getColor(),
                    calendarTag.getIsDefaultTag(),
                    calendarTag.getIsPersonalTag()
            ));
        }

        return result;
    }

//=============================================================================================================

    @Override
    public CalendarSchedule createCalendarSchedule(CreateCalendarScheduleDTO dto) {

        CalendarTag calendarTag = calendarTagRepository.findById(dto.CalendarTagId()).orElseThrow(
                () -> new NotFoundEntityException("해당 캘린더 태그가 없습니다")
        );

        return calendarScheduleRepository.save(new CalendarSchedule(
                dto.title(),
                dto.startDate(),
                dto.endDate(),
                calendarTag,
                dto.member()
        ));
    }

    @Override
    @Transactional
    public CalendarSchedule updateCalendarSchedule(UpdateCalendarScheduleDTO dto) {

        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(dto.CalendarScheduleId()).orElseThrow(
                () -> new NotFoundEntityException("해당 스케줄이 없습니다")
        );

        calendarSchedule.update(dto.title(), dto.startDate(), dto.endDate());

        return calendarSchedule;
    }

    @Override
    @Transactional
    public void deleteCalendarSchedule(Long id) {
        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 스케줄이 없습니다")
        );

        calendarScheduleRepository.delete(calendarSchedule);
    }

//=============================================================================================================

    @Override
    public CalendarPost createCalendarPost(CreateCalendarPostDTO dto) {
        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(dto.scheduleId()).orElseThrow(
                () -> new NotFoundEntityException("해당 일정이 없습니다")
        );

        CalendarPost save = calendarPostRepository.save(new CalendarPost(
                dto.title(),
                dto.body(),
                dto.author(),
                calendarSchedule,
                PostType.MEMOIR
        ));

        return save;
    }

    @Override
    @Transactional
    public void deleteCalendarPost(long id) {
        CalendarPost calendarPost = calendarPostRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 게시글이 없습니다")
        );
        photoAdvice.deletePhotos(calendarPost);
        calendarPostRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CalendarPost updateCalendarPost(UpdateCalendarPostDTO dto) {
        CalendarPost calendarPost = calendarPostRepository.findById(dto.id()).orElseThrow(
                () -> new NotFoundEntityException("해당 게시글이 없습니다")
        );

        List<Long> deletePhotoIdList = dto.deletePhoto();
        if (!deletePhotoIdList.isEmpty()) {
            for (Long id : deletePhotoIdList) {
                photoAdvice.deletePhoto(id);
            }
        }

        calendarPost.update(dto.title(), dto.body());

        return calendarPost;
    }

    //=============================================================================================================

    @Override
    public CalendarPostComment createCalendarPostComment(CCPCDTO dto) {
        CalendarPost calendarPost = calendarPostRepository.findById(dto.postId()).orElseThrow(
                () -> new NotFoundEntityException("해당 게시글이 없습니다")
        );

        CalendarPostComment save = calendarPostCommentRepository.save(new CalendarPostComment(
                dto.body(),
                dto.author(),
                calendarPost
        ));
        return save;
    }

    @Override
    @Transactional
    public void deleteCalendarPostComment(long id) {
        Member jwtMember = memberAdvice.findJwtMember();

        CalendarPostComment calendarPostComment = calendarPostCommentRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 코멘트가 없습니다")
        );

        if (!calendarPostComment.getAuthor().equals(jwtMember)) {
            throw new UnauthorizedActionException("본인만 수정할 수 있습니다.");
        }

        calendarPostCommentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CalendarPostComment updateCalendarPostComment(UpdateCPCDTO dto) {
        Member jwtMember = memberAdvice.findJwtMember();

        CalendarPostComment calendarPostComment = calendarPostCommentRepository.findById(dto.id()).orElseThrow(
                () -> new NotFoundEntityException("해당 코멘트가 없습니다")
        );

        if (!calendarPostComment.getAuthor().equals(jwtMember)) {
            throw new UnauthorizedActionException("본인만 수정할 수 있습니다.");
        }

        if (dto.body() == null || dto.body().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 본문은 비어 있을 수 없습니다.");
        }


        calendarPostComment.update(dto.body());
        return calendarPostComment;
    }

    @Override
    public CursorResult<CPCRes> getCalendarPostComments(Long postId, Long cursorId, String sort, Pageable page) {
        List<CPCRes> cpcRes = new ArrayList<>();
        final Page<CalendarPostComment> calendarPostComments = calendarPostCommentRepository.findCPCPageByCursorId(postId, cursorId, sort, page);
        ProfileSimpleRes profileSimpleRes;
        for (CalendarPostComment calendarPostComment : calendarPostComments) {
            Member commentedMember = calendarPostComment.getAuthor();
            profileSimpleRes = new ProfileSimpleRes(
                    commentedMember.getNickName(),
                    commentedMember.getProfilePhoto() == null ? null : commentedMember.getProfilePhoto().getS3Path(),
                    commentedMember.getId()
            );
            cpcRes.add(new CPCRes(
                    calendarPostComment.getId(),
                    calendarPostComment.getBody(),
                    profileSimpleRes,
                    calendarPostComment.getCreateDate(),
                    calendarPostComment.getLastModifiedDate()
            ));
        }
        cursorId = calendarPostComments.isEmpty() ?
                null : calendarPostComments.getContent().get(calendarPostComments.getContent().size() - 1).getId();
        return new CursorResult<>(cpcRes, cursorId, calendarPostComments.hasNext());
    }

    //=============================================================================================================

    @Override
    public CursorResult<PoomPoomLogInfo> getPoomPoomLogs(int page, int size) {
        Member jwtMember = memberAdvice.findJwtMember();
        return calendarPostRepository.findPoomPoomLogs(jwtMember,page-1, size);
    }

    @Override
    public List<PoomPoomLogInfo> getMonthlyPoomPoomLogs(int month) {
        Member jwtMember = memberAdvice.findJwtMember();
        return  calendarPostRepository.findPoomPoomLogsByMonth(jwtMember, month);
    }

    @Override
    public List<CalendarTagInfo> getDateTagInfo(Calendar calendar) {
        return List.of();
    }

    @Override
    @Transactional
    public List<CalendarScheduleInfo> getCalendarInfoByMonth(CalendarInfoByMonthDTO dto) {

        Calendar calendar = calendarRepository.findByAuth(dto.member()).orElseThrow(
                () -> new NotFoundCalendarException("해당 캘린더가 없습니다")
        );

        List<CalendarSchedule> scheduleList = calendarScheduleRepository.findByMonthAndYear(dto.month(), dto.year(), calendar);

        var result = new ArrayList<CalendarScheduleInfo>();

        for (CalendarSchedule schedule : scheduleList) {
            result.add(new CalendarScheduleInfo(
                    schedule.getCalendarTag().getInfoDTO(),
                    schedule.getId(),
                    schedule.getName(),
                    schedule.getStartDate(),
                    schedule.getEndDate(),
                    schedule.getAuthor().getAuthorInfo()
            ));
        }

        return result;
    }

    @Override
    @Transactional
    public CalendarPostDetailInfo getCalendarPostDetail(long id) {

        CalendarPost calendarPost = calendarPostRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 포스트를 찾을 수 없습니다")
        );

        List<Photo> photoList = photoRepository.findAllByCalendarPost(calendarPost);
        var photoPathList = new ArrayList<PhotoInPost>();
        for (Photo photo : photoList) {
            photoPathList.add(new PhotoInPost(
                    photo.getId(),
                    photo.getS3Path()
            ));
        }

        return new CalendarPostDetailInfo(
                photoPathList,
                calendarPost.getTitle(),
                calendarPost.getBody(),
                calendarPost.getAuthor().getAuthorInfo()
        );
    }

    @Override
    @Transactional
    public CalendarScheduleDetailInfo getCalendarScheduleDetail(long id) {

        CalendarSchedule calendarSchedule = calendarScheduleRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 스케줄이 없습니다")
        );

        List<CalendarPost> postList = calendarPostRepository.findAllByCalendarSchedule(calendarSchedule);
        var postInSchedule = new ArrayList<PostInSchedule>();

        for (CalendarPost calendarPost : postList) {
            List<PhotoInPost> photoS3Path = photoAdvice.getPhotoS3Path(calendarPost);
            postInSchedule.add(new PostInSchedule(
                    calendarPost.getId(), // 포스트 아이디
                    photoS3Path == null ? null : photoS3Path.get(0), //포스트 사진 데이터
                    calendarPost.getTitle() // 포스트 타이틀
            ));
        }

        return new CalendarScheduleDetailInfo(
                calendarSchedule.getName(),
                calendarSchedule.getAuthor().getAuthorInfo(),
                postInSchedule
        );
    }

}
