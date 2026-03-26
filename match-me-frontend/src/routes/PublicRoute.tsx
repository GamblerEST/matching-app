import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { JSX } from 'react';

const PublicRoute = ({ children }: { children: JSX.Element }) => {
    const { isLoggedIn } = useAuth();

    if (isLoggedIn) {
        return <Navigate to="/profile" replace />;
    }

    return children;
};

export default PublicRoute;