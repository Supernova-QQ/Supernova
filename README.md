### 🧪 서비스 테스트 가능 계정
- ID : `tester01@naver.com` ~ `tester09@naver.com` <br>
- PW : `tester01` ~ `tester09`

![image](https://github.com/user-attachments/assets/3ee7d56b-21f3-40ef-82aa-8418b5730e67)

## 📖Description

### 📍 프로젝트 소개

- Sci.Q 는 커뮤니티 기반 과학 질의응답 커뮤니티이션 서비스 입니다.

### 📍 기획의도

- 기존 과학 질의응답 서비스에 AI 답변봇을 더하여 질문에 대한 빠른 응답을 받을 수 있습니다.
- 뱃지 시스템의 도입으로 사용자의 활동에 대한 보상 체계를 갖추며 답변의 품질을 향상시킵니다.


## 1. 시연 영상

[Sci.Q 시연영상 바로가기(YouTube)](https://youtu.be/7-uru57d04I)

## 2. 주요 화면

![image](https://github.com/user-attachments/assets/7f3c6209-a574-4efb-801d-473bbe2cd058)
![image](https://github.com/user-attachments/assets/d204bbe9-0e67-45b1-926d-6aacc71126bf)
![image](https://github.com/user-attachments/assets/5512e222-2d0b-4a6b-94f7-906e8bdf378b)
![image](https://github.com/user-attachments/assets/21d427b2-b6d9-42df-b346-e781d4c75683)


## 3. 시스템 아키텍처

![image](https://github.com/user-attachments/assets/94c26c15-9e8a-40e2-aed4-e536c40b227d)


## 4. ERD

![image](https://github.com/user-attachments/assets/cd2f142d-7424-4516-8abf-d3f49303fe50)

## 5. 기술 스택

<img width="607" alt="image" src="https://github.com/user-attachments/assets/5f77f5a3-2be4-4cde-afe3-5efe6c91047d" />

## 6. API 명세서

### 💫 질문(커뮤니티, 일반 포함)
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- | 
| createQuestion | POST  | /api/questions | 게시글 생성 |
| getQuestion | GET | /api/questions/{q_id}  | 게시글 조회 |
| editQuestion | PUT | /api/questions/{q_id}  | 게시글 수정 |
| deleteQuestion | DELETE | /api/questions/{q_id}  | 게시글 삭제 |
| recommendQuestion | POST | /api/questions/{q_id}  | 게시글 추천 |
| getCommunityIdByQuestionId | GET | /api/questions/{q_id}/c_id  | 게시글이 속한 커뮤니티 조회 |
| getMyCommunities | GET | /api/questions/my-communities | 내 커뮤니티 목록 조회 |

### 💫 질문 목록
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getUnansweredOldQuestions | GET | /api/communities/{c_id}/unanswered-old-questions | 답변을 기다리는 질문 목록 오래된 순으로 정렬 |
| getUnansweredLatestQuestions | GET | /api/communities/{c_id}/unanswered-latest-questions | 답변을 기다리는 질문 목록 최신 순으로 정렬 |
| getUnansweredLatest4Questions | GET | /api/communities/{c_id}/unanswered-latest-4-questions | 답변을 기다리는 최신 4개 질문 조회 |
| allOldQuestionsByCommunity | GET | /api/communities/{c_id}/all-old-questions | 커뮤니티 별 전체 질문 목록 오래된 순으로 정렬 |
| allLatestQuestionsByCommunity | GET | /api/communities/{c_id}/all-latest-questions | 커뮤니티 별 전체 질문 목록 최신 순으로 정렬 |
| getNLatestQuestions | GET | /api/communities/N-latest-questions | 일반 게시판을 제외하고 전체 커뮤니티 최신 질문 N개 목록 |
| getNLatestQuestionsFromGeneralCommunity | GET | /api/communities/N-latest-questions-from-general | 일반 게시판 최신 질문 4개 목록 |

### 💫 답변
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- | 
| createAnswer | POST | /api/questions/{q_id}/answers  | 답변 생성 |
| getAnswer | GET | /api/questions/{q_id}/answers/{a_id}  | 답변 조회 |
| updateAnswer | PUT | /api/questions/{q_id}/answers/{a_id}  | 답변 수정 |
| deleteAnswer | DELETE | /api/questions/{q_id}/answers/{a_id}  | 답변 삭제 |
| acceptAnswer | PATCH | /api/questions/{q_id}/answers/{a_id}  | 답변 채택 |
| getAnswerList | POST  | /api/questions/{q_id}/answers | 답변 목록 조회 |
| recommendAnswer | POST | /api/questions/{q_id}/answers/{a_id}/recommendation  | 답변 추천 |


### 💫 AI 답변
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getAiComment | GET | /api/questions/{q_id}/ai-answers | AI 답변 조회 |
| updateAiComment | PUT | /api/questions/{q_id}/ai-answers | AI 답변 재생성 |

### 💫 공지사항
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| createNotice | POST | /api/notices | 공지사항 생성 |
| getNotice | GET | /api/notices/{noticeId} | 공지사항 조회 |
| updateNotice | PUT | /api/notices/{noticeId} | 공지사항 수정 |
| deleteNotice | DELETE | /api/notices/{noticeId} | 공지사항 삭제 |
| getAllNotices | GET | /api/notices | 공지사항 목록 최신순으로 조회 |
| getPinnedNotices | GET | /api/notices/pinned-list | 고정된 공지사항 목록 조회 |

### 💫 뉴스
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| createNews | POST | /api/news | 알림 생성 |
| getNews | GET | /api/news/{newsId} | 알림 조회 |
| updateNews | PUT | /api/news/{newsId} | 알림 수정 |
| deleteNews | DELETE | /api/news/{newsId} | 알림 삭제 |
| getAllNews | GET | /api/news | 전체 알림 목록 조회 |
| getViewedNews | GET | /api/news/viewed-list | 확인한 알림 목록 조회 |
| getUnViewedNews | GET | /api/news/unviewed-list | 미확인 알림 목록 조회 |

### 💫 커뮤니티
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| createCommunity | POST | /api/communities | 커뮤니티 생성 |
| getCommunityInfo | GET | /api/communities/{c_id} | 커뮤니티 정보 조회 |
| updateCommunity | PUT | /api/communities/{c_id} | 커뮤니티 수정 |
| joinCommunity | POST | /api/communities/{c_id} | 커뮤니티 가입 |
| leaveCommunity | DELETE | /api/communities/{c_id} | 커뮤니티 탈퇴 |
| dormantCommunity | PATCH | /api/communities/{c_id} | 커뮤니티 휴면 전환 |
| allOldCommunities | GET | /api/communities/all-old-communities | 오래된 순 커뮤니티 목록 조회 |
| allLatestCommunities | GET | /api/communities/all-latest-communities | 최신 등록 순 커뮤니티 목록 조회 |

### 💫 사용자
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| resetPassword | POST | /api/users/reset-password | 사용자 비밀번호 초기화 |
| registerUser | POST | /api/users/register | 회원가입 |
| changePassword | POST | /api/users/change-password | 사용자 비밀번호 변경 |
| changeNickname | POST | /api/users/change-nickname | 사용자 닉네임 변경 |
| getNickname | GET | /api/users/nickname | 사용자 닉네임 조회 |
| getAllUsers | GET | /api/users/all | 회원 목록 조회 |
| deleteUser | DELETE | /api/users/delete | 회원 정보 삭제 |

### 💫 해시태그
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getHashtags | GET | /api/questions/{q_id}/hashtags | 게시글에 대한 해시태그 목록 조회 |
| saveHashtag | POST | /api/questions/{q_id}/hashtags | 게시글에 대한 해시태그 등록 |

### 💫 마이페이지
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| createAnswerWithQuestionTitle | POST | /api/my/questions/{q_id}/answers/with-question-title | 질문 제목과 함께 답변 생성 |
| getMyQuestions | GET | /api/my/questions | 마이페이지 정보 조회 |
| getMyCommunities | GET | /api/my/communities | 내 커뮤니티 목록 조회 |
| getMyAnswers | GET | /api/my/answers | 마이페이지 답변 조회 |

### 💫 S3
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| uploadImage | POST | /api/images | 이미지 업로드 |

### 💫 북마크
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| addBookmark | POST | /api/communities/{c_id}/bookmarks | 북마크 추가 |
| removeBookmark | DELETE | /api/communities/{c_id}/bookmarks | 북마크 삭제 |
| getBookmarkedQuestions | GET | /api/communities/{c_id}/bookmarks/questions | 로그인한 유저의 질문 북마크 리스트 조회 |
| getBookmarkedAnswers | GET | /api/communities/{c_id}/bookmarks/answers | 로그인한 유저의 답변 북마크 리스트 조회 |
| isQuestionBookmarked | GET | /api/communities/{c_id}/bookmarks/questions/{questionId} | 특정 질문 북마크 상태 조회 |
| isAnswerBookmarked | GET | /api/communities/{c_id}/bookmarks/answers/{answerId} | 특정 답변 북마크 상태 조회 |

### 💫 인증
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| refreshAccessToken | POST | /api/auth/refresh | RefreshToken을 사용한 AccessToken 재발급 |
| logout | POST | /api/auth/logout | 로그아웃 요청 |
| login | POST | /api/auth/login | 로그인 |

### 💫 검색
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| searchKeyword | GET | /api/search | 키워드 검색 |

### 💫 Rate Limit
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getRateLimitStatus | GET | /api/rate-limit/ai-answers | AI 답변 생성 남은 횟수 조회 |
| getQuestionRateLimitStatus | GET | /api/rate-limit/ai-answers/questions/{questionId} | 특정 게시글 내부 AI 답변 생성 남은 횟수 조회 |

### 💫 인기 목록
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getPreMonthTopNCommunities | GET | /api/main/premonth-topN-communities | 근 30일 최다 방문객수 커뮤니티 5개 조회 |
| getPreMonthTop5Questions | GET | /api/main/premonth-top5-questions | 근 30일 최다 조회수 질문 5개 조회 |
| getPreDayTop10Hashtag | GET | /api/main/preday-top10-hashtag | 전날 최다 사용된 해시태그 10개 목록 조회 |
| getPreDayMostViewedQuestion | GET | /api/main/preday-most-viewed-question | 전날 최다 조회수 질문 1개 조회 |
| getPreDayMostRecommendedAnswer | GET | /api/main/preday-most-recommended-answer | 전날 최다 추천수 답변 1개 조회 |

### 💫 커뮤니티 인기 목록
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getPopularQuestions | GET | /api/communities/{c_id}/popular-questions | 커뮤니티 내부 인기 게시글 전체 목록 조회 |
| getPopular4Questions | GET | /api/communities/{c_id}/popular-4-questions | 커뮤니티 내부 인기 게시글 4개 목록 조회 |

### 💫 커뮤니티 통계
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getDailyVisitorCount | GET | /api/communities/{communityId}/visitors/daily | 커뮤니티 일일 방문자수 조회 |

### 💫 뱃지
| 🏷 NAME | ⚙ METHOD | 📎 URL | 📖 DESCRIPTION |
| --- | --- | --- | --- |
| getUserBadgeStatus | GET | /api/badges | 사용자의 뱃지 상태를 반환 |


