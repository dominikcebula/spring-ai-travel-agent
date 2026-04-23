const STORAGE_KEY = 'shopping-agent.conversationId';

function generateUUID(): string {
    return crypto.randomUUID();
}

export function getConversationId(): string {
    const existing = localStorage.getItem(STORAGE_KEY);
    if (existing) {
        return existing;
    }
    const created = generateUUID();
    localStorage.setItem(STORAGE_KEY, created);
    return created;
}

export function resetConversationId(): string {
    const created = generateUUID();
    localStorage.setItem(STORAGE_KEY, created);
    return created;
}
