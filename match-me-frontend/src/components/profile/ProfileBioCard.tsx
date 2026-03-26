import { useState, useEffect } from 'react';
import { Card, Form, Button } from 'react-bootstrap';

import { UserBio, BioTypeaheadField } from "../../interfaces/UserBio";
import { GetMyBio, UpdateBio } from '../../api/bioApi';

import MySpinner from '../MySpinner';

import type { Option } from "react-bootstrap-typeahead/types/types";
import { Typeahead } from "react-bootstrap-typeahead";
import "react-bootstrap-typeahead/css/Typeahead.css";

export default function ProfileBioCard() {

    const [isEditing, setIsEditing] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [formValues, setFormValues] = useState<UserBio | null>(null);
    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        if (!fieldTags.hobbies.length) {
            newErrors.hobbies = "Hobbies are required";
        }

        if (!fieldTags.interests.length) {
            newErrors.interests = "Interests are required";
        }

        if (!fieldTags.foodPreferences.length) {
            newErrors.foodPreferences = "Food Preferences are required";
        }

        if (!fieldTags.musicTaste.length) {
            newErrors.musicTaste = "Music Taste are required";
        }

        if (!fieldTags.personalityType.length) {
            newErrors.personalityType = "Personality Types are required";
        }

        if (!fieldTags.lookingFor.length) {
            newErrors.lookingFor = "Looking For are required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    const [fieldTags, setFieldTags] = useState<Record<BioTypeaheadField, string[]>>({
        hobbies: [],
        interests: [],
        foodPreferences: [],
        musicTaste: [],
        personalityType: [],
        lookingFor: []
    });

    useEffect(() => {
        async function fetchProfileBio() {
            try {
                const data: UserBio = await GetMyBio();
                setFormValues(data);
                setFieldTags({
                    hobbies: data.hobbies?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
                    interests: data.interests?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
                    foodPreferences: data.foodPreferences?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
                    musicTaste: data.musicTaste?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
                    personalityType: data.personalityType?.split(",").map(v => v.trim()).filter(Boolean) ?? [],
                    lookingFor: data.lookingFor?.split(",").map(v => v.trim()).filter(Boolean) ?? []
                });
            } catch (error) {
                console.log(error);
            }
        };
        fetchProfileBio();
    }, []);

    const handleTypeaheadChange = (
        field: BioTypeaheadField,
        selected: Option[]
    ) => {
        const values = selected.map(item => typeof item === "string" ? item.trim() : String(item.label).trim());
        const uniqueValues = Array.from(new Map(values.map(v => [v.toLowerCase(), v])).values());
        setFieldTags(prev => ({ ...prev, [field]: uniqueValues }));
        setFormValues(prev => prev ? { ...prev, [field]: uniqueValues.join(", ") } : prev);
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;
        if (!formValues) return;
        try {
            await UpdateBio(formValues);
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
                    Account Biographical Data
                    {isEditing ? (
                        <Button
                            type="submit"
                            variant="success"
                        >
                            Save
                        </Button>
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
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Hobbies
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formHobbies"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter hobbies"
                            options={["running", "walking", "cooking"]}
                            selected={fieldTags["hobbies"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("hobbies", selected)}
                        />
                        {errors.hobbies && (
                            <div className="text-danger small mt-1">
                                {errors.hobbies}
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Interests
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formInterests"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter interests"
                            options={["traveling", "coding"]}
                            selected={fieldTags["interests"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("interests", selected)}
                        />
                        {errors.interests && (
                            <div className="text-danger small mt-1">
                                {errors.interests}
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Food Preferences
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formFoodPreferences"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter Food Preferences"
                            options={["potato", "meat", "eggs"]}
                            selected={fieldTags["foodPreferences"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("foodPreferences", selected)}
                        />
                        {errors.foodPreferences && (
                            <div className="text-danger small mt-1">
                                {errors.foodPreferences}
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Music Taste
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formMusicTaste"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter Music Taste"
                            options={["hip-hop", "classics", "rock", "rap"]}
                            selected={fieldTags["musicTaste"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("musicTaste", selected)}
                        />
                        {errors.musicTaste && (
                            <div className="text-danger small mt-1">
                                {errors.musicTaste}
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Personality Type
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formPersonalityType"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter Personality Type"
                            options={["kind", "funny", "loving", "responsible"]}
                            selected={fieldTags["personalityType"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("personalityType", selected)}
                        />
                        {errors.personalityType && (
                            <div className="text-danger small mt-1">
                                {errors.personalityType}
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group className="small mb-1">
                        <Form.Label>
                            Looking For
                            <span className="text-danger">
                                *
                            </span>
                        </Form.Label>
                        <Typeahead
                            id="formLookingFor"
                            maxResults={10}
                            multiple
                            allowNew
                            placeholder="Enter Looking For"
                            options={["wife", "husband", "friend", "companion"]}
                            selected={fieldTags["lookingFor"]}
                            disabled={!isEditing}
                            onChange={selected => handleTypeaheadChange("lookingFor", selected)}
                        />
                        {errors.lookingFor && (
                            <div className="text-danger small mt-1">
                                {errors.lookingFor}
                            </div>
                        )}
                    </Form.Group>
                </Card.Body>
            </Form>
        </Card>);
}