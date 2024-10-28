document.addEventListener('DOMContentLoaded', function() {
  const questionCreateBtn = document.getElementById('create-question-btn');

  if (questionCreateBtn) {
    questionCreateBtn.addEventListener('click', function() {
      // 현재 URL에서 communityId 확인
      const currentPath = window.location.pathname;
      const communityMatch = currentPath.match(/\/communities\/info\/(\d+)/);

      if (communityMatch) {
        // 커뮤니티 내부에서 질문 생성
        const communityId = communityMatch[1];
        window.location.href = `/questions/create?communityId=${communityId}`;
      } else {
        // 일반 질문 생성
        window.location.href = '/questions/create';
      }
    });
  }
});