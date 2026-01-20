import React from 'react';
import ChatBot, {Flow, Params} from "react-chatbotify";
import './App.css';

const API_BASE_URL = process.env.REACT_APP_API_URL || '';

async function callAgent(userInput: string): Promise<string> {
    const response = await fetch(
        `${API_BASE_URL}/api/v1/agent?userInput=${encodeURIComponent(userInput)}`
    );
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    return response.text();
}

const flow: Flow = {
    start: {
        message: "Hello! I'm your travel assistant. I can help you search and book flights, hotels, and rental cars. How can I assist you today?",
        path: "chat"
    },
    chat: {
        message: async (params: Params) => {
            try {
                return await callAgent(params.userInput);
            } catch (error) {
                return "Sorry, I'm having trouble connecting to the server. Please try again later.";
            }
        },
        path: "chat"
    }
};

function App() {
    return (
        <div className="app">
            <header className="header">
                <div className="header-content">
                    <div className="logo">
                        <img src="/travel-agent-icon.svg" alt="Travel Agent" className="logo-icon"/>
                        <div className="logo-text">
                            <h1>SkyWay Travel</h1>
                            <span className="tagline">AI-Powered Travel Assistant</span>
                        </div>
                    </div>
                    <nav className="nav">
                        <span className="nav-item active">
                            <FlightIcon/> Flights
                        </span>
                        <span className="nav-item">
                            <HotelIcon/> Hotels
                        </span>
                        <span className="nav-item">
                            <CarIcon/> Cars
                        </span>
                    </nav>
                </div>
            </header>

            <main className="main">
                <div className="hero">
                    <h2>Your Journey Starts Here</h2>
                    <p>Chat with our AI assistant to find and book the perfect flights, hotels, and rental cars for your
                        trip.</p>
                </div>

                <div className="chat-container">
                    <ChatBot
                        flow={flow}
                        settings={{
                            general: {
                                embedded: true,
                                primaryColor: "#3b82f6",
                                secondaryColor: "#1e40af"
                            },
                            header: {
                                title: "Travel Assistant",
                                avatar: "/travel-agent-icon.svg"
                            },
                            chatHistory: {
                                storageKey: "travel_agent_chat"
                            },
                            chatWindow: {
                                showScrollbar: true
                            },
                            botBubble: {
                                showAvatar: true
                            }
                        }}
                    />
                </div>
            </main>

            <footer className="footer">
                <p>&copy; 2025 SkyWay Travel. Powered by Spring AI.</p>
            </footer>
        </div>
    );
}

const FlightIcon = () => (
    <svg viewBox="0 0 24 24" fill="currentColor" className="icon">
        <path
            d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
    </svg>
);

const HotelIcon = () => (
    <svg viewBox="0 0 24 24" fill="currentColor" className="icon">
        <path
            d="M7 13c1.66 0 3-1.34 3-3S8.66 7 7 7s-3 1.34-3 3 1.34 3 3 3zm12-6h-8v7H3V5H1v15h2v-3h18v3h2v-9c0-2.21-1.79-4-4-4z"/>
    </svg>
);

const CarIcon = () => (
    <svg viewBox="0 0 24 24" fill="currentColor" className="icon">
        <path
            d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"/>
    </svg>
);

export default App;
