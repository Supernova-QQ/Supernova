document.addEventListener('DOMContentLoaded', function () {
  const dropdown = document.querySelector('.custom-dropdown');
  const selected = dropdown.querySelector('.dropdown-selected');
  const options = dropdown.querySelectorAll('.dropdown-option');
  const selectedText = document.getElementById('selected-text');

  // 드롭다운 토글
  selected.addEventListener('click', () => {
    dropdown.classList.toggle('open');
  });

  // 옵션 선택
  options.forEach(option => {
    option.addEventListener('click', () => {
      const value = option.dataset.value;
      selectedText.textContent = option.textContent.trim();

      // 체크마크 업데이트
      options.forEach(opt => opt.classList.remove('selected'));
      option.classList.add('selected');

      dropdown.classList.remove('open');

      // 검색 타입 값 업데이트 (검색 시 사용)
      dropdown.dataset.value = value;
    });
  });

  // 외부 클릭 시 드롭다운 닫기
  document.addEventListener('click', (e) => {
    if (!dropdown.contains(e.target)) {
      dropdown.classList.remove('open');
    }
  });

  // 검색 버튼 클릭 시 이벤트 업데이트
  document.getElementById('search-btn').addEventListener('click', () => {
    const searchType = dropdown.dataset.value || 'TITLE_OR_CONTENT';
    const searchKeyword = document.getElementById('search-input').value.trim();

    if (!searchKeyword) {
      alert('검색어를 입력해주세요.');
      return;
    }

    if (searchKeyword) {
      window.location.href = `/search?search-keyword=${encodeURIComponent(
          searchKeyword)}&search-type=${searchType}`;
    }
  });
});
