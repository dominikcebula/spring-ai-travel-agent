export async function sendMessage(
    userInput: string,
    conversationId: string
): Promise<string> {
    const url = `/api/v1/agent?userInput=${encodeURIComponent(
        userInput
    )}&conversationId=${encodeURIComponent(conversationId)}`;

    const response = await fetch(url, {method: 'GET'});

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
    }

    return response.text();
}
