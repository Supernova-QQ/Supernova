console.log("community script loaded");

document.addEventListener('DOMContentLoaded', function () {
    // URL에서 communityId 추출
    const pathSegments = window.location.pathname.split('/');
    const communityId = pathSegments[pathSegments.length - 1]; // URL의 마지막 부분을 ID로 간주

    // "내 노트" 버튼 클릭 시 처리
    const myNoteButton = document.getElementById('my-note-button');
    if (myNoteButton) {
        myNoteButton.addEventListener('click', function () {
            window.location.href = `/communities/my-note/${communityId}`;
        });
    }

    // "질의응답" 버튼 클릭 시 처리
    const qnaButton = document.getElementById('qna-button');
    if (qnaButton) {
        qnaButton.addEventListener('click', function () {
            window.location.href = `/communities/info/${communityId}`;
        });
    }
});
