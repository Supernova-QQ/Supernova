package com.hanshin.supernova.ai_comment.util;

public class Prompt {

    public static String generateAiRequest(String title, String content) {
        String prompt =
                """
                너는 과학 전반에 대해 굉장히 잘 아는 전문가야.
                자세히 설명하지 않아도 되니까 아는대로 대답해줘.
                단, 사실만을 답해야 해.
                모든 콘텐츠에 '', "", (), [] 또는 이모티콘을 사용하지 말아야 해.
                다음 제목과 내용이 질문이야. 이에 대해 '답변'을 json 형식으로 답변해줘.
                이 때 답변은 근거와 결론을 포함하되, 근거와 결론을 구분하지 말고, 하나의 연속된 설명으로 JSON 형식으로 응답해줘.
                제목: %s
                내용: %s
                """;
        return String.format(prompt, title, content);
    }

    public static String regenerateAiRequest(String title, String content, String preAnswer) {
        String prompt =
                """
                너는 과학 전반에 대해 굉장히 잘 아는 전문가야.
                자세히 설명하지 않아도 되니까 아는대로 대답해줘.
                단, 사실만을 답해야 해.
                모든 콘텐츠에 '', "", (), [] 또는 이모티콘을 사용하지 말아야 해.
                다음 '제목'과 '내용'이 질문이야. 이에 대해 '이전답변'보다 발전된 '답변'을 json 형식으로 답변해줘.
                이 때 답변은 근거와 결론을 반드시 포함해서 20자 이상으로 답변하되, 근거와 결론을 구분하지 말고, 하나의 연속된 설명으로 JSON 형식으로 응답해줘.
                또 이전의 답변이 과학적 사실에 근거하지 않아서 다시 요청했을 수도 있으니, 반드시 정확한 사실에 근거하여 답변 부탁해.
                제목: %s
                내용: %s
                이전답변: %s
                """;
        return String.format(prompt, title, content, preAnswer);
    }
}
