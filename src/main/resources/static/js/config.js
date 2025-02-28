console.log("config script loaded");


let CONFIG = {
  API: {
    // BASE_URL: 'http://localhost:8080',
    BASE_URL: 'http://13.124.80.116:8080',
    ENDPOINTS: {
      COMMUNITIES: '/api/communities',
      QUESTIONS: '/api/questions',
      SEARCH: '/api/search',
      MAIN: '/api/main',
      NEWS: '/api/news'
      // 다른 엔드포인트들도 여기에 추가
    }
  },
  AUTH: {
    TOKEN_KEY: 'X-QQ-ACCESS-TOKEN',
    DEFAULT_TOKEN: localStorage.getItem('ACCESS_TOKEN_HEADER_KEY')
  },
  PAGINATION: {
    DEFAULT_PAGE: 0,
    DEFAULT_SIZE: 10
  }
};
export default CONFIG;

// URL 생성 헬퍼 함수
function getApiUrl(endpoint, ...params) {
  let url = Config.API.BASE_URL + Config.API.ENDPOINTS[endpoint];
  if (params.length > 0) {
    url += '/' + params.join('/');
  }
  return url;
}

// 공통 헤더 가져오기
function getDefaultHeaders() {
  return {
    'Content-Type': 'application/json',
    [Config.AUTH.TOKEN_KEY]: Config.AUTH.DEFAULT_TOKEN
  };
}

// 외부에서 사용할 수 있도록 내보내기
// export { getApiUrl, getDefaultHeaders };
