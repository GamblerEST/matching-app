import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Modal, Button, Form, Container, Alert } from 'react-bootstrap';

import { AuthRegister } from '../api/authApi';

import { useAuth } from '../context/AuthContext';
import { connectStompWithAuth } from "../components/chat/chatSocket";

export default function Register() {

    const [showError, setShowErrorBox] = useState(false);
    const [errorText, setErrorText] = useState('');

    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const handleClose = () => navigate('/');

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        if (password !== confirmPassword) {
            setErrorText("Passwords Do Not Match.");
            setShowErrorBox(true);
            return;
        }
        try {
            const responseData = await AuthRegister({ email, username, password });
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
    }

    return (
        <div>
            <div className="homepage-bg">
                <Container className="overlay d-flex flex-column justify-content-center align-items-center text-center">
                    <Modal show={true} onHide={handleClose} centered>
                        <Modal.Header closeButton>
                            <Modal.Title>Register</Modal.Title>
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

                                <Form.Group className="mb-3" controlId="formUsername">
                                    <Form.Label>Username</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Enter username"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
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

                                <Form.Group className="mb-3" controlId="formConfirmPassword">
                                    <Form.Label>Confirm Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        placeholder="Confirm password"
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        required
                                    />
                                </Form.Group>

                                <Button variant="primary" type="submit" className="w-100">
                                    Register
                                </Button>
                                <div className="text-center">
                                    <Button
                                        variant="link"
                                        className="p-0"
                                        as={Link as any}
                                        to="/login"
                                    >
                                        <p className="small fw-bold mt-2 pt-1 mb-0">
                                            Already have an account? Login
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
