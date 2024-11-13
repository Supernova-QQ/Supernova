function fetchData(url) {
    const accessToken = localStorage.getItem('ACCESS_TOKEN_HEADER_KEY');
    return fetch(url, {
        method: 'GET',
        headers: {
            'X-QQ-ACCESS-TOKEN': accessToken ? accessToken : '', // ACCESS_TOKEN_HEADER_KEY 사용
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        });
}
