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
        // 로그인 상태일 때만 fetchProfile 호출
        fetchProfile();
    } else {
        // accessToken이 없으면 로그아웃 상태 요소 표시
        document.getElementById("loggedInElements").style.display = "none";
        document.getElementById("loggedOutElements").style.display = "flex";
    }


    // 드롭다운과 로그아웃 관련 요소들 가져오기
    const settingBtn = document.getElementById("setting-btn");
    const dropdownMenu = document.getElementById("setting-dropdown-menu");
    const logoutBtn = document.getElementById("logout-btn");

    // setting-btn 클릭 시 드롭다운 메뉴 토글
    settingBtn.addEventListener("click", function () {
        dropdownMenu.style.display = dropdownMenu.style.display === "block" ? "none" : "block";
    });

    // 로그아웃 버튼 클릭 시 로그아웃 처리
    logoutBtn.addEventListener("click", async function () {
        try {
            // 서버에 로그아웃 요청
            await fetch("/api/auth/logout", {
                method: "POST",
                headers: {
                    "X-QQ-ACCESS-TOKEN": localStorage.getItem("ACCESS_TOKEN_HEADER_KEY"),
                    "Content-Type": "application/json"
                }
            });

            // 클라이언트 로컬스토리지에서 AccessToken 삭제 및 로그인 페이지로 리디렉션
            localStorage.removeItem("ACCESS_TOKEN_HEADER_KEY");
            window.location.href = "/";
        } catch (error) {
            console.error("로그아웃 중 오류 발생:", error);
        }
    });

    // 페이지 외부 클릭 시 드롭다운 닫기
    document.addEventListener("click", function (event) {
        if (!settingBtn.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.style.display = "none";
        }
    });
});

// 프로필 데이터 가져오기 및 표시
async function fetchProfile() {
    console.log("fetchProfile 실행됨");
    try {
        const response = await fetchWithAuth("/api/users/profile", {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        if (!response.ok) throw new Error("Failed to fetch profile data");

        const data = await response.json();
        console.log("fetchProfile data:", data);

        displayProfile(data); // 프로필 데이터를 화면에 표시
    } catch (error) {
        console.error("Error fetching profile:", error);
    }
}

// 프로필 데이터를 화면에 표시
function displayProfile(data) {
    console.log("displayProfile data:", data);

    const profile = document.getElementById("profile");
    const profilePicture = document.getElementById("profile-picture");
    const profileImageUrl = data.data?.profileImageUrl || "/images/basic-profile.png";

    console.log("프로필 이미지 URL:", profileImageUrl);

    profile.src = profileImageUrl; // 프로필 이미지 업데이트
    profilePicture.src = profileImageUrl; // 프로필 이미지 업데이트
}

// AccessToken 자동 갱신을 위한 fetchWithAuth 함수 정의
async function fetchWithAuth(url, options = {}) {
    // 헤더에 AccessToken 추가
    let accessToken = localStorage.getItem("ACCESS_TOKEN_HEADER_KEY");
    console.log("fetchWithAuth AccessToken : {}",accessToken);

    if (accessToken) {
        options.headers = {
            ...options.headers,
            "X-QQ-ACCESS-TOKEN": accessToken
        };
    }

    let response = await fetch(url, options);
    console.log("API 응답 상태 코드:", response.status);

    // AccessToken 만료로 인해 401 Unauthorized 반환 시
    if (response.status === 401) {
        console.log("AccessToken이 만료됨. RefreshToken으로 새로고침 시도 중...");

        // RefreshToken으로 AccessToken 재발급 요청
        const refreshResponse = await fetch("/api/auth/refresh", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (refreshResponse.ok) {
            // 새 AccessToken 저장
            const newAccessToken = await refreshResponse.text(); // 서버가 새 AccessToken만 반환한다고 가정
            localStorage.setItem("ACCESS_TOKEN_HEADER_KEY", newAccessToken);
            console.log("AccessToken이 재발급되었습니다 : " , newAccessToken)

            // 기존 요청 재시도
            options.headers["X-QQ-ACCESS-TOKEN"] = newAccessToken;
            response = await fetch(url, options);
        } else {
            // RefreshToken이 유효하지 않을 경우 로그아웃 처리
            console.log("RefreshToken이 만료되었습니다. 재로그인이 필요합니다.");

            // 서버에 로그아웃 요청
            await fetch("/api/auth/logout", {
                method: "POST",
                headers: {
                    "X-QQ-ACCESS-TOKEN": accessToken  // 기존 만료된 AccessToken을 로그아웃 요청에 사용
                }
            });

            // 클라이언트 로컬스토리지에서 AccessToken 삭제 및 로그인 페이지로 이동
            localStorage.removeItem("ACCESS_TOKEN_HEADER_KEY");
            window.location.href = "/";
        }
    }

    return response;
}