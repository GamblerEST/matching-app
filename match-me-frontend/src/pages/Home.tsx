import React from 'react';
import { Link } from 'react-router-dom';
import { Container, Button } from 'react-bootstrap';

export default function Home() {
    return (
        <div className="homepage-bg">
            <Container className="overlay d-flex flex-column justify-content-center align-items-center text-center">
                <h1 className="mb-3">Match-Me</h1>
                <p className="mb-4">Find matches based on your interests</p>
                <Button as={Link as any} to="/register" className="btn-pink btn-lg">
                    Get Started
                </Button>
            </Container>
        </div>
    );
}
