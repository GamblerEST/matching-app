import {Card, Button, Form} from 'react-bootstrap';

export default function ProfileDetailsCard() {
    return (
        <Card className="mb-4">
            <Card.Header className="d-flex justify-content-between align-items-center">
                Account Details
                <Button
                    variant="outline-primary"
                >
                    Edit
                </Button>
            </Card.Header>
            <Card.Body>
                <Form>
                    <Form.Group className="small mb-1" controlId="formEmail">
                        <Form.Label>Email address</Form.Label>
                        <Form.Control
                            type="email"
                            placeholder="Enter email"
                            value="Email Adress"
                            disabled
                        />
                    </Form.Group>
                    <Form.Group className="small mb-1" controlId="formUsername">
                        <Form.Label>Username (how your name will appear to other users on the site)</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Enter username"
                            value="Username"
                            disabled
                        />
                    </Form.Group>
                </Form>
            </Card.Body>
        </Card>
    );
}