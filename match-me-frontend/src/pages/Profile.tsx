import React from 'react';

import { Container, Row, Col, Alert } from 'react-bootstrap';

import ProfileBioCard from '../components/profile/ProfileBioCard';
import ProfileLocationCard from '../components/profile/ProfileLocationCard';
import ProfileInfoCard from '../components/profile/ProfileInfoCard';

export default function Profile() {
    return (
        <Container>
            <hr className="mt-0 mb-2" />
            <Alert key="info" variant="info" className="mb-2">
                Users must complete their profile before they are able to see recommendations or connect with other users.
            </Alert>
            <Row>
                <Col sm={4} >
                    <ProfileInfoCard />
                </Col>
                <Col sm={4}>
                    <ProfileBioCard />
                </Col>
                <Col sm={4} >
                    <ProfileLocationCard />
                </Col>
                
            </Row>
        </Container>
    );
}