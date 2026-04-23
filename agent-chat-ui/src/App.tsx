import React, {useEffect, useRef, useState} from 'react';
import './App.css';
import {sendMessage} from './api';
import {getConversationId, resetConversationId} from './conversation';

type Role = 'user' | 'agent';

interface Message {
    id: string;
    role: Role;
    content: string;
}

const WELCOME_MESSAGE: Message = {
    id: 'welcome',
    role: 'agent',
    content:
        "Hi! I'm your ElectroShop assistant. Ask me about laptops, monitors, tablets, smartphones, smartwatches, keyboards, mice — anything we sell. How can I help you today?",
};

const SUGGESTIONS = [
    'Recommend a laptop for software development under $1500',
    'Compare 27-inch 4K monitors',
    'Best smartwatch for running',
    'I need a quiet mechanical keyboard',
];

function makeMessageId(): string {
    return `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
}

function App() {
    const [conversationId, setConversationId] = useState<string>(() =>
        getConversationId()
    );
    const [messages, setMessages] = useState<Message[]>([WELCOME_MESSAGE]);
    const [input, setInput] = useState('');
    const [isSending, setIsSending] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const scrollRef = useRef<HTMLDivElement | null>(null);
    const textareaRef = useRef<HTMLTextAreaElement | null>(null);

    useEffect(() => {
        scrollRef.current?.scrollTo({
            top: scrollRef.current.scrollHeight,
            behavior: 'smooth',
        });
    }, [messages, isSending]);

    useEffect(() => {
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto';
            textareaRef.current.style.height = `${Math.min(
                textareaRef.current.scrollHeight,
                200
            )}px`;
        }
    }, [input]);

    async function handleSend() {
        const trimmed = input.trim();
        if (!trimmed || isSending) {
            return;
        }

        const userMessage: Message = {
            id: makeMessageId(),
            role: 'user',
            content: trimmed,
        };

        setMessages((prev) => [...prev, userMessage]);
        setInput('');
        setIsSending(true);
        setError(null);

        try {
            const reply = await sendMessage(trimmed, conversationId);
            const agentMessage: Message = {
                id: makeMessageId(),
                role: 'agent',
                content: reply,
            };
            setMessages((prev) => [...prev, agentMessage]);
        } catch (e) {
            const msg = e instanceof Error ? e.message : 'Unknown error';
            setError(`Sorry, I couldn't reach the assistant. ${msg}`);
        } finally {
            setIsSending(false);
        }
    }

    function handleKeyDown(event: React.KeyboardEvent<HTMLTextAreaElement>) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            handleSend();
        }
    }

    function handleNewConversation() {
        const fresh = resetConversationId();
        setConversationId(fresh);
        setMessages([WELCOME_MESSAGE]);
        setError(null);
    }

    function handleSuggestionClick(suggestion: string) {
        setInput(suggestion);
        textareaRef.current?.focus();
    }

    return (
        <div className="app">
            <header className="app__header">
                <div className="app__brand">
                    <div className="app__logo" aria-hidden="true">
                        EC
                    </div>
                    <div>
                        <h1 className="app__title">ElectroShop Assistant</h1>
                        <p className="app__subtitle">
                            Laptops · Monitors · Tablets · Phones · Wearables · Accessories
                        </p>
                    </div>
                </div>
                <button
                    type="button"
                    className="app__new-chat"
                    onClick={handleNewConversation}
                    title="Start a new conversation"
                >
                    New conversation
                </button>
            </header>

            <main className="chat" ref={scrollRef}>
                <div className="chat__inner">
                    {messages.map((m) => (
                        <MessageBubble key={m.id} message={m}/>
                    ))}
                    {isSending && <TypingIndicator/>}
                    {error && <div className="chat__error">{error}</div>}
                    {messages.length <= 1 && !isSending && (
                        <div className="suggestions">
                            {SUGGESTIONS.map((s) => (
                                <button
                                    key={s}
                                    type="button"
                                    className="suggestions__item"
                                    onClick={() => handleSuggestionClick(s)}
                                >
                                    {s}
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </main>

            <footer className="composer">
                <div className="composer__inner">
          <textarea
              ref={textareaRef}
              className="composer__input"
              placeholder="Ask about a product, compare options, check specs..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              rows={1}
              disabled={isSending}
          />
                    <button
                        type="button"
                        className="composer__send"
                        onClick={handleSend}
                        disabled={isSending || !input.trim()}
                    >
                        {isSending ? 'Sending…' : 'Send'}
                    </button>
                </div>
                <div className="composer__hint">
                    Press Enter to send · Shift+Enter for a new line
                </div>
            </footer>
        </div>
    );
}

function MessageBubble({message}: { message: Message }) {
    const isUser = message.role === 'user';
    return (
        <div className={`bubble bubble--${isUser ? 'user' : 'agent'}`}>
            <div className="bubble__avatar" aria-hidden="true">
                {isUser ? 'You' : 'AI'}
            </div>
            <div className="bubble__content">{message.content}</div>
        </div>
    );
}

function TypingIndicator() {
    return (
        <div className="bubble bubble--agent bubble--typing">
            <div className="bubble__avatar" aria-hidden="true">
                AI
            </div>
            <div className="bubble__content">
                <span className="dot"/>
                <span className="dot"/>
                <span className="dot"/>
            </div>
        </div>
    );
}

export default App;
