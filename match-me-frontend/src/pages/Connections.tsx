import { useState, useEffect } from "react";
import { Container, Tabs, Tab, Row } from "react-bootstrap";

import { UserProfile } from "../interfaces/ProfileInfo";
import { UserBio } from "../interfaces/UserBio";
import { GetUserProfile } from "../api/profileApi";
import { GetUserBio } from "../api/bioApi";
import { GetPendingConnectionRequest, GetConnections } from "../api/connectionsApi";
import ReccomendationCard from "../components/recommendations/RecommendationCard";
import MySpinner from "../components/MySpinner";

export default function Connections() {

    const [userProfilesConnected, setUserProfilesConnected] = useState<UserProfile[]>([]);
    const [userProfilesPending, setUserProfilesPending] = useState<UserProfile[]>([]);

    const [loading, setLoading] = useState(true);
    const [key, setKey] = useState("connections");

    useEffect(() => {
        async function fetchPendingConnectionRequest() {
            try {
                const requests = await GetPendingConnectionRequest();
                const profilesWithBios = await Promise.all(
                    requests.map(async (request: {
                        id: number;
                        requesterId: number;
                    }) => {
                        const profile = await GetUserProfile(request.requesterId);
                        const bio: UserBio = await GetUserBio(request.requesterId);
                        return {
                            ...profile,
                            bio,
                            connectionId: request.id,
                        };
                    })
                );
                setUserProfilesPending(profilesWithBios);
            } catch (error) {
                console.log(error);
            }
        }
        async function fetchConnections() {
            try {
                const requests = await GetConnections();
                const profilesWithBios = await Promise.all(
                    requests.map(async (request: {
                        id: number;
                    }) => {
                        const profile = await GetUserProfile(request.id);
                        const bio: UserBio = await GetUserBio(request.id);
                        return {
                            ...profile,
                            bio
                        };
                    })
                );
                setUserProfilesConnected(profilesWithBios);
            } catch (error) {
                console.log(error);
            } finally {
                setLoading(false);
            }
        }
        fetchConnections();
        fetchPendingConnectionRequest();
    }, []);

    const handleAcceptConnection = (acceptedProfile: UserProfile) => {
        setUserProfilesPending(prev =>
            prev.filter(p => p.userId !== acceptedProfile.userId)
        );
        setUserProfilesConnected(prev => [...prev, acceptedProfile]);
    };

    const handleDisconnectConnection = (acceptedProfile: UserProfile) => {
        setUserProfilesConnected(prev =>
            prev.filter(p => p.userId !== acceptedProfile.userId)
        );
    };

    const handleRejectConnection = (acceptedProfile: UserProfile) => {
        setUserProfilesPending(prev =>
            prev.filter(p => p.userId !== acceptedProfile.userId)
        );
    };

    if (loading) return (
        <MySpinner />
    );

    return (
        <Container>
            <hr className="mt-0 mb-2" />
            <Tabs
                id="uncontrolled-tab-example"
                activeKey={key}
                onSelect={(k) => {
                    if (k) setKey(k);
                }}
                className="mb-3"
            >
                <Tab eventKey="connections" title="Connections">
                    {userProfilesConnected.length === 0 &&
                        <Row className="g-2">
                            <div className="d-flex justify-content-center align-items-center">
                                <h1>No Connections.</h1>
                            </div>
                        </Row>
                    }
                    <Row xs={1} sm={2} md={3} lg={5} className="g-2">
                        {userProfilesConnected.map((UserProfile) => {
                            return (
                                <ReccomendationCard
                                    key={UserProfile.userId}
                                    userId={UserProfile.userId}
                                    displayName={UserProfile.displayName}
                                    avatarUrl={UserProfile.avatarUrl ?? "images/noImage.png"}
                                    aboutMe={UserProfile.aboutMe}
                                    bio={UserProfile.bio}
                                    connected={true}
                                    onDisconnectConnection={() => handleDisconnectConnection(UserProfile)}
                                />
                            );
                        })}
                    </Row>
                </Tab>
                <Tab eventKey="incomingconnections" title="Incoming Connections">
                    {userProfilesPending.length === 0 &&
                        <Row className="g-2">
                            <div className="d-flex justify-content-center align-items-center">
                                <h1>No Incoming Connections.</h1>
                            </div>
                        </Row>
                    }
                    <Row xs={1} sm={2} md={3} lg={5} className="g-2">
                        {userProfilesPending.map((UserProfile) => {
                            return (
                                <ReccomendationCard
                                    key={UserProfile.userId}
                                    userId={UserProfile.userId}
                                    displayName={UserProfile.displayName}
                                    avatarUrl={UserProfile.avatarUrl ?? "images/noImage.png"}
                                    aboutMe={UserProfile.aboutMe}
                                    bio={UserProfile.bio}
                                    connectionId={UserProfile.connectionId}
                                    onRejectConnection={() => handleRejectConnection(UserProfile)}
                                    onAcceptConnection={() => handleAcceptConnection(UserProfile)}
                                />
                            );
                        })}
                    </Row>
                </Tab>
            </Tabs>
        </Container>
    );
}