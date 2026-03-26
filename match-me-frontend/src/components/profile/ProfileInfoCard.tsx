import { useState, useEffect } from 'react';

import { Card, Button, Image, Form } from 'react-bootstrap';

import { ProfileInfo } from '../../interfaces/ProfileInfo';
import { GetMyProfile, UpdateProfile, UpdateMyProfilePicture } from '../../api/profileApi';
import MySpinner from '../MySpinner';

export default function ProfileInfoCard() {

    const [isEditing, setIsEditing] = useState(false);
    const [imageString, setImageString] = useState<string | undefined>(undefined);
    const [formValues, setFormValues] = useState<ProfileInfo | null>(null);
    const [imageFile, setImageFile] = useState<File | undefined>(undefined);

    useEffect(() => {
        async function fetchProfileInfo() {
            try {
                const data: ProfileInfo = await GetMyProfile();
                setFormValues(data);
            } catch (error) {
                console.log(error);
            }
        };
        fetchProfileInfo();
    }, []);

    const handleChange = <K extends keyof ProfileInfo>(
        key: K, value: ProfileInfo[K]) => {
        setFormValues(prev => prev ? { ...prev, [key]: value } : prev);
    };

    const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        setImageFile(file);
        if (!file) return;
        const reader = new FileReader();
        reader.onloadend = () => {
            setImageString(reader.result as string);
            handleChange("avatarUrl", file.name);
        };
        reader.readAsDataURL(file);
    };

    const handleFileDelete = (e: React.FormEvent) => {
        handleChange("avatarUrl", null);
        setImageFile(undefined);
        const input = document.getElementById("formFile") as HTMLInputElement | null;
        if (input) {
            input.value = "";
        }
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!formValues) return;
        try {
            await UpdateProfile(formValues);
            if (imageFile !== undefined) {
                const responseData = await UpdateMyProfilePicture(imageFile);
                const newImageUrl = responseData.avatarUrl;
                handleChange("avatarUrl", newImageUrl);
                setImageFile(undefined);
            }
            setIsEditing(false);
        } catch (error) {
            console.error(error);
        }
    };

    if (!formValues) return <MySpinner />;

    return (
        <Card className="mb-4">
            <Form onSubmit={handleSave}>
                <Card.Header className="d-flex justify-content-between align-items-center">
                    Profile Info
                    {isEditing ? (
                        <>
                            {formValues.avatarUrl &&
                                <Button
                                    type="button"
                                    variant="outline-primary"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        handleFileDelete(e);
                                    }}
                                >
                                    Remove Image
                                </Button>
                            }
                            <Button
                                type="submit"
                                variant="success"
                            >
                                Save
                            </Button>
                        </>
                    ) : (
                        <Button
                            type="button"
                            variant="outline-primary"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                setIsEditing(true);
                            }}
                        >
                            Edit
                        </Button>
                    )}
                </Card.Header>
                <Card.Body>
                    {isEditing &&
                        <Form.Group className="mb-2" controlId="formFile">
                            <Form.Label>Upload Profile Image</Form.Label>
                            <Form.Control
                                type="file"
                                accept="image/*"
                                onChange={handleFileUpload}
                            />
                        </Form.Group>
                    }
                    <div className="text-center">
                        {imageFile && isEditing &&
                            <div className="avatar-wrapper-profile">
                                <img
                                    src={imageString}
                                    alt="avatar"
                                    className="avatar-img"
                                />
                            </div>
                        }
                        {formValues.avatarUrl && !imageFile &&
                            <div className="avatar-wrapper-profile">
                                <img
                                    src={formValues.avatarUrl}
                                    alt="avatar"
                                    className="avatar-img"
                                />
                            </div>
                        }
                        {!formValues.avatarUrl && isEditing &&
                            <div className="avatar-wrapper-profile">
                                <img
                                    src="images/noImage.png"
                                    alt="avatar"
                                    className="avatar-img"
                                />
                            </div>
                        }
                        {!formValues.avatarUrl && !isEditing &&
                            <div className="avatar-wrapper-profile">
                                <img
                                    src="images/noImage.png"
                                    alt="avatar"
                                    className="avatar-img"
                                />
                            </div>
                        }
                    </div>
                    <Form.Group className="small mb-1 mt-3" controlId="formDisplayName">
                        <Form.Label>Username</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Enter Display Name"
                            value={formValues?.displayName ?? ""}
                            disabled={!isEditing}
                            required={isEditing}
                            onChange={e => handleChange("displayName", e.target.value)}
                        />
                    </Form.Group>
                    <Form.Group className="small mb-1 mt-3" controlId="formAboutMe">
                        <Form.Label>About Me</Form.Label>
                        <Form.Control
                            type="text"
                            as="textarea" rows={3}
                            placeholder="Enter About Me Info"
                            value={formValues?.aboutMe ?? ""}
                            disabled={!isEditing}
                            required={isEditing}
                            onChange={e => handleChange("aboutMe", e.target.value)}
                        />
                    </Form.Group>
                </Card.Body>
            </Form>
        </Card>
    );
}