import CONFIG from '/static/js/config.js';

let currentPage = CONFIG.PAGINATION.DEFAULT_PAGE;
const pageSize = CONFIG.PAGINATION.DEFAULT_SIZE;

document.addEventListener('DOMContentLoaded', () => {
  const newsModal = document.getElementById('news-modal');
  const newsInfoModal = document.getElementById('news-info-modal');
  const bellBtn = document.getElementById('news-bell-btn');
  const closeButtons = document.getElementsByClassName('close');
  const loadMoreBtn = document.getElementById('load-more-btn');
  const backBtn = document.getElementById('news-info-back-btn');

  if (bellBtn) {
    bellBtn.addEventListener('click', () => {
      newsModal.style.display = 'block';
      currentPage = 0;
      document.getElementById('news-list').innerHTML = '';
      fetchNews();
    });
  } else {
    console.error("Bell button not found");
  }

  for (let closeBtn of closeButtons) {
    closeBtn.onclick = () => {
      newsModal.style.display = 'none';
      newsInfoModal.style.display = 'none';
    }
  }

  window.onclick = (event) => {
    if (event.target == newsModal) {
      newsModal.style.display = 'none';
    }
    if (event.target == newsInfoModal) {
      newsInfoModal.style.display = 'none';
    }
  }

  loadMoreBtn.onclick = fetchNews;

  backBtn.onclick = () => {
    newsInfoModal.style.display = 'none';
    newsModal.style.display = 'block';
  }
});

const baseURL = CONFIG.API.BASE_URL;

function fetchNews() {
  const url = baseURL + `/api/news?page=${currentPage}&size=${pageSize}`;

  fetchWithAuth(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    if (data.data && Array.isArray(data.data.content)) {
      displayNews(data.data.content, currentPage === 0);
      currentPage++;
      const loadMoreBtn = document.getElementById('load-more-btn');
      if (data.data.last) {
        loadMoreBtn.style.display = 'none';
      } else {
        loadMoreBtn.style.display = 'block';
      }
    } else {
      throw new Error('Data structure is not as expected');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the news:', error);
    document.getElementById(
        'news-list').innerHTML = '<p>알림을 불러오는 데 실패했습니다~</p>';
  });
}

function fetchNewsDetails(newsId) {
  const url = baseURL + `/api/news/${newsId}`;

  fetchWithAuth(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(responseData => {
    if (responseData && responseData.data) {
      displayNewsDetails(responseData.data);
      updateNewsItemStatus(newsId, true);
    } else {
      throw new Error('Data structure is not as expected');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the news details:', error);
  });
}

function updateNewsItemStatus(newsId, viewed) {
  const newsItem = document.querySelector(`.news-item[data-id="${newsId}"]`);
  if (newsItem) {
    newsItem.classList.remove('unviewed');
    newsItem.classList.add('viewed');
  }
}

function displayNews(newsItems) {
  const newsList = document.getElementById('news-list');

  if (!Array.isArray(newsItems)) {
    console.error('newsItems is not an array:', newsItems);
    newsList.innerHTML = '<p>알림을 불러오는 데 실패했습니다.</p>';
    return;
  }

  if (newsItems.length === 0) {
    newsList.innerHTML = '<p>알림이 없습니다.</p>';
    return;
  }

  newsItems.forEach(news => {
    const newsItem = document.createElement('div');
    newsItem.className = `news-item ${news.viewed ? 'viewed' : 'unviewed'}`;
    newsItem.textContent = news.title;
    newsItem.setAttribute('data-id', news.id);
    newsItem.onclick = () => fetchNewsDetails(news.id);
    newsList.appendChild(newsItem);
  });
}

function displayNewsDetails(news) {
  document.getElementById('news-info-title').textContent = news.title;
  document.getElementById('news-info-content').textContent = news.content;
  document.getElementById('news-info-created-at').textContent = `${new Date(
      news.createdAt).toLocaleString()}`;

  const relatedBtn = document.getElementById('news-info-related-btn');
  if (news.hasRelatedContent) {
    relatedBtn.style.display = 'block';
    relatedBtn.onclick = () => navigateToRelatedContent(news.type, news.relatedContentId);
  } else {
    relatedBtn.style.display = 'none';
  }

  document.getElementById('news-modal').style.display = 'none';
  document.getElementById('news-info-modal').style.display = 'block';
}

function navigateToRelatedContent(type, relatedContentId) {
  switch (type) {
    case 'AUTHORITY':
      // 권한 관련 페이지로 이동 (예: 사용자 프로필 페이지)
      window.location.href = `/profile/${relatedContentId}`;
      break;
    case 'COMMUNITY':
      // 커뮤니티 페이지로 이동
      window.location.href = `/communities/info/${relatedContentId}`;
      break;
    case 'QUESTION':
      // 질문 페이지로 이동
      window.location.href = `/questions/info/${relatedContentId}`;
      break;
    case 'ANSWER':
      // 답변이 포함된 질문 페이지로 이동
      window.location.href = `/questions/info/${relatedContentId}`;
      break;
    case 'BADGE':
      // 뱃지 정보 페이지로 이동
      window.location.href = `/badges/${relatedContentId}`;
      break;
    default:
      console.error('Unknown news type:', type);
      // 기본적으로 홈페이지나 알림 목록 페이지로 이동
      window.location.href = '/';
  }
}
