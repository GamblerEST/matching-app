import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from 'react-bootstrap/Button';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';    
import ChatNotification from './chat/ChatNotification';

export default function MyNavbar() {
    const { isLoggedIn, logout } = useAuth();

    return (
        <Navbar collapseOnSelect expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand as={Link} to="/">
                    Match-Me
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="ms-auto">
                        {!isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/login"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                            >
                                Login
                            </Button>
                        }
                        {!isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/register"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                            >
                                Register
                            </Button>
                        }
                        {isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/recommendations"
                                variant="outline-primary"
                                className="mt-2 mb-2 mb-lg-0 mt-lg-0 me-lg-2 mt-lg-0 btn btn-outline-primary"
                            >
                                Recommendations
                            </Button>
                        }
                        {isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/connections"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                            >
                                Connections
                            </Button>
                        }
                        {isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/chats"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                            >
                                Chats
                            </Button>
                        }
                        {isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/profile"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                            >
                                Profile
                            </Button>
                        }
                        {isLoggedIn &&
                           <ChatNotification />
                        }
                        {isLoggedIn &&
                            <Button
                                as={Link as any}
                                to="/"
                                variant="outline-primary"
                                className="mb-2 mb-lg-0 me-lg-2 btn btn-outline-primary"
                                onClick={logout}
                            >
                                Logout
                            </Button>
                        }
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
