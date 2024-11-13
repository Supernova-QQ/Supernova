console.log("header script loaded");

document.addEventListener("DOMContentLoaded", function() {

    // // 토큰을 localStorage에서 삭제
    // localStorage.removeItem("ACCESS_TOKEN_HEADER_KEY");

    // localStorage에서 accessToken 확인
    const accessToken = localStorage.getItem("ACCESS_TOKEN_HEADER_KEY");

    // 로그인 상태에 따라 요소 표시
    if (accessToken) {
        // accessToken이 존재하면 로그인 상태 요소 표시
        document.getElementById("loggedInElements").style.display = "flex";
        document.getElementById("loggedOutElements").style.display = "none";
    } else {
        // accessToken이 없으면 로그아웃 상태 요소 표시
        document.getElementById("loggedInElements").style.display = "none";
        document.getElementById("loggedOutElements").style.display = "flex";
    }
});

// AccessToken 자동 갱신을 위한 fetchWithAuth 함수 정의
async function fetchWithAuth(url, options = {}) {
    // 헤더에 AccessToken 추가
    let accessToken = localStorage.getItem("ACCESS_TOKEN_HEADER_KEY");
    if (accessToken) {
        options.headers = {
            ...options.headers,
            "X-QQ-ACCESS-TOKEN": accessToken
        };
    }

    let response = await fetch(url, options);

    // AccessToken 만료로 인해 401 Unauthorized 반환 시
    if (response.status === 401) {
        console.log("AccessToken이 만료됨. RefreshToken으로 새로고침 시도 중...");

        // 새 AccessToken 요청
        const refreshResponse = await fetch("/api/auth/refresh", { method: "POST" });

        if (refreshResponse.ok) {
            // 새 AccessToken 저장
            const newAccessToken = await refreshResponse.text(); // 서버가 새 AccessToken만 반환한다고 가정
            localStorage.setItem("ACCESS_TOKEN_HEADER_KEY", newAccessToken);

            // 기존 요청 재시도
            options.headers["X-QQ-ACCESS-TOKEN"] = newAccessToken;
            response = await fetch(url, options);
        } else {
            // RefreshToken이 유효하지 않을 경우 로그아웃 처리
            console.log("RefreshToken이 만료되었습니다. 재로그인이 필요합니다.");
            localStorage.removeItem("ACCESS_TOKEN_HEADER_KEY");
            window.location.href = "/auth/login";
        }
    }

    return response;
}