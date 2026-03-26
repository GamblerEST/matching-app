import { useState, useEffect } from 'react';

import { Alert, Container, Row } from "react-bootstrap";
import ReccomendationCard from "../components/recommendations/RecommendationCard";
import { GetRecommendations } from "../api/recommendationsApi";
import { GetUserProfile } from '../api/profileApi';
import MySpinner from '../components/MySpinner';
import { GetUserBio } from '../api/bioApi';

import { UserBio } from '../interfaces/UserBio';
import { UserProfile } from '../interfaces/ProfileInfo';

export default function Recommendations() {

    const [showError, setShowErrorBox] = useState(false);
    const [errorText, setErrorText] = useState('');

    const [userProfiles, setUserProfiles] = useState<UserProfile[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchRecommendations() {
            try {
                // FETCH PROFILE IDS
                const profileIds = await GetRecommendations();
                const ids = profileIds.map((p: { id: number }) => p.id);
                // FETCH USER PROFILES && FETCH USER BIOS
                const profilesWithBios = await Promise.all(
                    ids.map(async (id: number) => {
                        const profile = await GetUserProfile(id);
                        const bio: UserBio = await GetUserBio(id);
                        return { ...profile, bio };
                    })
                );
                // ONE OBJECT STORING USER PROFILES AND BIO
                setUserProfiles(profilesWithBios);
            } catch (error: any) {
                if (error.status === 403) {
                    setShowErrorBox(true);
                    setErrorText(error.message);
                }
            } finally {
                setLoading(false);
            }
        };
        fetchRecommendations();
    }, []);

    const handleDismissConnection = (Profile: UserProfile) => {
        setUserProfiles(prev =>
            prev.filter(p => p.userId !== Profile.userId)
        );
    };

    if (loading) return (
        <MySpinner />
    );

    if (errorText) {
        return (
            <Container>
                <hr className="mt-0 mb-2" />
                <Row className="g-2">
                    <div className="d-flex justify-content-center align-items-center">
                        <Alert key="danger" variant="danger">
                            {errorText}
                        </Alert>
                    </div>
                </Row>
            </Container>
        );
    }

    if (userProfiles.length === 0) {
        return (
            <Container>
                <hr className="mt-0 mb-2" />
                <Row className="g-2">
                    <div className="d-flex justify-content-center align-items-center">
                        <h1>No Recommendations Found !</h1>
                    </div>
                </Row>
            </Container>
        );
    }

    return (
        <Container>
            <hr className="mt-0 mb-2" />
            <Row xs={1} sm={2} md={3} lg={5} className="g-2">
                {userProfiles.map((UserProfile) => {
                    return (
                        <ReccomendationCard
                            key={UserProfile.userId}
                            userId={UserProfile.userId}
                            displayName={UserProfile.displayName}
                            avatarUrl={UserProfile.avatarUrl ?? "images/noImage.png"}
                            aboutMe={UserProfile.aboutMe}
                            bio={UserProfile.bio}
                            onDismissConnection={() => handleDismissConnection(UserProfile)}
                        />
                    );
                })}
            </Row>
        </Container>
    );
}