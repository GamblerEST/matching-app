import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Modal, Button, Form, Container, Alert } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import { connectStompWithAuth } from "../components/chat/chatSocket";

import { AuthLogin } from '../api/authApi';

export default function Login() {

    const { login } = useAuth();
    const navigate = useNavigate();

    const [showError, setShowErrorBox] = useState(false);
    const [errorText, setErrorText] = useState('');

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleClose = () => navigate('/');

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        try {
            const responseData = await AuthLogin({ email, password });
            const token = responseData.token;
            const userId = responseData.userId;
            if (token && userId) {
                login(token);
                connectStompWithAuth();
                navigate('/profile');
            }
        } catch (error: any) {
            setErrorText(error.message);
            setShowErrorBox(true);
        }
        //handleClose();
    };

    return (
        <div>
            <div className="homepage-bg">
                <Container className="overlay d-flex flex-column justify-content-center align-items-center text-center">
                    <Modal show={true} onHide={handleClose} centered>
                        <Modal.Header closeButton>
                            <Modal.Title>Login</Modal.Title>
                        </Modal.Header>

                        <Modal.Body>
                            <Form onSubmit={handleSubmit}>
                                {showError &&
                                    <Alert key="danger" variant="danger">
                                        {errorText}
                                    </Alert>
                                }
                                <Form.Group className="mb-3" controlId="formEmail">
                                    <Form.Label>Email address</Form.Label>
                                    <Form.Control
                                        type="email"
                                        placeholder="Enter email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3" controlId="formPassword">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        placeholder="Enter password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                                <Button variant="primary" type="submit" className="w-100">
                                    Login
                                </Button>
                                <div className="text-center">
                                    <Button
                                        variant="link"
                                        className="p-0"
                                        as={Link as any}
                                        to="/register"
                                    >
                                        <p className="small fw-bold mt-2 pt-1 mb-0">
                                            Don't have an account? Register
                                        </p>
                                    </Button>
                                </div>
                            </Form>
                        </Modal.Body>
                    </Modal>
                </Container>
            </div>
        </div>
    );
}
