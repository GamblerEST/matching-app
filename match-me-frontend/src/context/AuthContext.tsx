// context/AuthContext.tsx
import { createContext, useContext, useState } from 'react';
import { disconnectStomp } from '../components/chat/chatSocket';

interface AuthContextType {
    isLoggedIn: boolean;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(
        !!localStorage.getItem('jwt')
    );

    const login = (token: string) => {
        localStorage.setItem('jwt', token);
        setIsLoggedIn(true);
    };

    const logout = () => {
        localStorage.removeItem('jwt');
        setIsLoggedIn(false);
        disconnectStomp();
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};
