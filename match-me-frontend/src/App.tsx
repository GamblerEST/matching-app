import { useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';

import PublicRoute from './routes/PublicRoute';
import ProtectedRoute from './routes/ProtectedRoute';

import MyNavbar from './components/Navbar';

import Home from './pages/Home';
import Register from './pages/Register';
import Login from './pages/Login';
import Profile from './pages/Profile';
import Recommendations from './pages/Recommendations';
import Connections from './pages/Connections';
import Chat from './pages/Chat';
import Chats from './pages/Chats';

import { connectStompWithAuth, disconnectStomp, stompClient } from "./components/chat/chatSocket";

export default function App() {

    useEffect(() => {
        const onConnect = () => {
            stompClient.subscribe("/user/queue/messages", (message) => {
                const payload = JSON.parse(message.body);
                window.dispatchEvent(new CustomEvent("newMessage", { detail: payload }));
            });
            stompClient.subscribe("/user/queue/typing", (message) => {
                const payload = JSON.parse(message.body);
                window.dispatchEvent(new CustomEvent("typing", { detail: payload }));
            });
        };

        stompClient.onConnect = onConnect;

        if (!stompClient.active) {
            stompClient.activate();
        }

        return () => {
            stompClient.onConnect = () => { };
        };
    }, []);

    useEffect(() => {
        if (localStorage.getItem("jwt")) {
            connectStompWithAuth();
        }else{
            disconnectStomp();
        }
    });

    return (
        <div>
            <MyNavbar />

            <Routes>
                <Route
                    path="/"
                    element={<Home />}
                />
                <Route
                    path="/login"
                    element={
                        <PublicRoute>
                            <Login />
                        </PublicRoute>
                    }
                />
                <Route
                    path="/register"
                    element={
                        <PublicRoute>
                            <Register />
                        </PublicRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/recommendations"
                    element={
                        <ProtectedRoute>
                            <Recommendations />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/connections"
                    element={
                        <ProtectedRoute>
                            <Connections />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/chat/:chatRoomId"
                    element={
                        <ProtectedRoute>
                            <Chat />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/chats"
                    element={
                        <ProtectedRoute>
                            <Chats />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </div>
    );
}