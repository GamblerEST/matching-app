import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Modal, ButtonGroup, Form } from "react-bootstrap";

import { UserProfile } from "../../interfaces/ProfileInfo";
import { BioTypeaheadField } from "../../interfaces/UserBio";

import { DismissRecommendation } from '../../api/recommendationsApi';
import { SendConnectionRequest, AcceptConnectionRequest, RejectConnectionRequest, DisconnectConnection } from '../../api/connectionsApi';
import { CreateChatRoom } from '../../api/chatApi';

import AlertModal from '../AlertModal';
import { Typeahead } from "react-bootstrap-typeahead";
import "react-bootstrap-typeahead/css/Typeahead.css";

export default function ReccomendationCard(props: UserProfile) {

    const [showModal, setShowModal] = useState(false);
    const [modalTitle, setModalTitle] = useState("");
    const [modalMessage, setModalMessage] = useState("");
    const [modalVariant, setModalVariant] = useState<"success" | "danger" | "warning">("warning");
    const [pendingAction, setPendingAction] = useState<(() => void) | null>(null);

    const navigate = useNavigate();
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const [fieldTags, setFieldTags] = useState<Record<BioTypeaheadField, string[]>>({
        hobbies: [],
        interests: [],
        foodPreferences: [],
        musicTaste: [],
        personalityType: [],
        lookingFor: []
    });

    useEffect(() => {
        setFieldTags({
            hobbies: props.bio.hobbies?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
            interests: props.bio.interests?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
            foodPreferences: props.bio.foodPreferences?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
            musicTaste: props.bio.musicTaste?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
            personalityType: props.bio.personalityType?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
            lookingFor: props.bio.lookingFor?.split(",").map(v => v.trim()).filter(Boolean) ?? []
        });
    }, [props.bio]);

    const handleDisconnectConnection = (e: React.FormEvent) => {
        e.preventDefault();
        setModalTitle("Disconnect Connection Request");
        setModalMessage("Are you sure you want to disconnect this connection?");
        setModalVariant("warning");
        setPendingAction(() => async () => {
            try {
                const statusCode: number = await DisconnectConnection(props.userId);
                if (statusCode === 204) {
                    props.onDisconnectConnection?.();
                }
                setModalTitle("Success");
                setModalMessage("Connection disconnected successfully!");
                setModalVariant("success");
                setPendingAction(null);
            } catch (error: any) {
                setModalTitle("Error");
                setModalMessage(error.message);
                setModalVariant("danger");
                setPendingAction(null);
            }
        });
        setShowModal(true);
    };

    function handleDismissConnection(e: React.FormEvent) {
        e.preventDefault();
        setModalTitle("Reject Connection Request");
        setModalMessage("Are you sure you want to reject this connection?");
        setModalVariant("warning");
        setPendingAction(() => async () => {
            try {
                const statusCode = await RejectConnectionRequest(props.connectionId);
                if (statusCode === 200) {
                    props.onRejectConnection?.();
                }
                setModalTitle("Success");
                setModalMessage("Connection dismissed successfully!");
                setModalVariant("success");
                setPendingAction(null);
            } catch (error: any) {
                setModalTitle("Error");
                setModalMessage(error.message);
                setModalVariant("danger");
                setPendingAction(null);
            }
        });
        setShowModal(true);
    }

    function handleDismissRecommendation(e: React.FormEvent) {
        e.preventDefault();
        setModalTitle("Dismiss Connection Request");
        setModalMessage("Are you sure you want to dismiss this connection?");
        setModalVariant("warning");
        setPendingAction(() => async () => {
            try {
                const statusCode = await DismissRecommendation(props.userId);
                if (statusCode === 200) {
                    props.onDismissConnection?.();
                }
                setModalTitle("Success");
                setModalMessage("Connection dismissed successfully!");
                setModalVariant("success");
                setPendingAction(null);
            } catch (error: any) {
                setModalTitle("Error");
                setModalMessage(error.message);
                setModalVariant("danger");
                setPendingAction(null);
            }
        });
        setShowModal(true);
    }

    async function handleSendConnection(e: React.FormEvent) {
        e.preventDefault();
        try {
            const statusCode: number = await SendConnectionRequest(props.userId);
            if (statusCode === 200) {
                setModalTitle("Connection Request");
                setModalMessage("Connection Request Sent Successfully!");
                setModalVariant("success");
                setPendingAction(null);
            }
        } catch (error: any) {
            setModalTitle("Error");
            setModalMessage(error.message);
            setModalVariant("danger");
            setPendingAction(null);
        }
        setShowModal(true);
    };

    async function handleAcceptConnection(e: React.FormEvent) {
        e.preventDefault();
        try {
            const statusCode: number = await AcceptConnectionRequest(props.connectionId);
            if (statusCode === 200) {
                setModalTitle("Connection Request");
                setModalMessage("Connection Request Accepted Successfully!");
                setModalVariant("success");
                setPendingAction(null);
                setTimeout(() => {
                    props.onAcceptConnection?.();
                }, 5000); //after 5 sec del the elemnt alerts closes aswell
            }
        } catch (error: any) {
            setModalTitle("Error");
            setModalMessage(error.message);
            setModalVariant("danger");
            setPendingAction(null);
        }
        setShowModal(true);
    };

    const handleOpenChat = async (e: React.FormEvent) => {
        try{
            const data = await CreateChatRoom(props.userId);
            navigate("/chat/" + data.id + "?otheruserid=" + props.userId);
        }catch(error) {
            console.log(error);
        }
    };

    return (
        <div>
            <AlertModal
                show={showModal}
                onClose={() => setShowModal(false)}
                title={modalTitle}
                message={modalMessage}
                variant={modalVariant}
                onConfirm={pendingAction ?? undefined}
                confirmText="Yes"
            />
            <Card>
                <Card.Body>
                    <div className="avatar-wrapper">
                        <img
                            src={props.avatarUrl}
                            alt="avatar"
                            className="avatar-img"
                        />
                    </div>
                    <Card.Title>{props.displayName}</Card.Title>
                    <div className="d-grid">
                        <Button variant="secondary" className="mt-2" size="lg" onClick={handleShow}>View Profile</Button>
                    </div>
                    {props.connected === true &&
                        <div className="d-grid">
                            <Button variant="danger" className="mt-2" size="lg" onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                handleDisconnectConnection(e);
                            }}>
                                Disconnect
                            </Button>
                        </div>
                    }
                    {props.connectionId &&
                        <>
                            <div className="d-grid">
                                <ButtonGroup className="mt-2">
                                    <Button variant="danger"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            handleDismissConnection(e);
                                        }}>
                                        Dismiss
                                    </Button>
                                    <Button variant="success"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            handleAcceptConnection(e);
                                        }}>
                                        Accept
                                    </Button>
                                </ButtonGroup>
                            </div>
                        </>
                    }
                    {!props.connectionId && !props.connected &&
                        <>
                            <div className="d-grid">
                                <ButtonGroup className="mt-2">
                                    <Button variant="danger"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            handleDismissRecommendation(e);
                                        }}>
                                        Dismiss
                                    </Button>
                                    <Button variant="success"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            handleSendConnection(e);
                                        }}>
                                        Connect
                                    </Button>
                                </ButtonGroup>
                            </div>
                        </>
                    }
                </Card.Body>
            </Card>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <div className="w-100 d-flex align-items-center justify-content-center position-relative">
                        <Modal.Title>
                            User Profile
                        </Modal.Title>
                        {props.connected && (
                            <Button
                                variant="secondary"
                                size="lg"
                                className="position-absolute start-0"
                                onClick={(e) => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    setShow(false);
                                    handleOpenChat(e);
                                }}
                            >
                                Chat
                            </Button>
                        )}
                    </div>
                </Modal.Header>
                <Modal.Body>
                    <p>About Me</p>
                    <Form.Control
                        id="formTextare"
                        type="text"
                        as="textarea" rows={3}
                        value={props.aboutMe}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Hobbies</p>
                    <Typeahead
                        id="formHobbies"
                        multiple
                        options={[]}
                        selected={fieldTags["hobbies"]}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Hobbies</p>
                    <Typeahead
                        id="formInterests"
                        multiple
                        options={[]}
                        selected={fieldTags["interests"]}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Food Preferences</p>
                    <Typeahead
                        id="formFoodPreferences"
                        multiple
                        options={[]}
                        selected={fieldTags["foodPreferences"]}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Music Taste</p>
                    <Typeahead
                        id="formMusicTaste"
                        multiple
                        options={[]}
                        selected={fieldTags["musicTaste"]}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Personality Type</p>
                    <Typeahead
                        id="formPersonalityType"
                        multiple
                        options={[]}
                        selected={fieldTags["personalityType"]}
                        disabled={true}
                    />
                    <p className="mt-2 mb-2">Looking For</p>
                    <Typeahead
                        id="formLookingFor"
                        multiple
                        options={[]}
                        selected={fieldTags["lookingFor"]}
                        disabled={true}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
        </div >
    );
}