import { useState, useEffect } from 'react';

import { Card, Form, Button } from 'react-bootstrap';

import { ProfileLocation, ProfileLocationForm } from '../../interfaces/ProfileLocation';

import { GetMyLocation, UpdateLocation } from '../../api/locationApi';
import MySpinner from '../MySpinner';

import cities from '../../cities.json';

type City = {
    name: string;
    latitude: number;
    longitude: number;
}

export default function ProfileLocationCard() {

    const cityList: City[] = cities.map((c) => ({
        name: c.city,
        latitude: Number(c.lat),
        longitude: Number(c.lng)
    }));

    const [isEditing, setIsEditing] = useState(false);
    const [formValues, setFormValues] = useState<ProfileLocationForm | null>(null);

    const [isLoadingGeoLocation, setIsLoadingGeoLocation] = useState(false);

    useEffect(() => {
        async function fetchProfileLocation() {
            try {
                const data: ProfileLocation = await GetMyLocation();
                setFormValues({
                    id: data.id,
                    city: data.city,
                    latitude: String(data.latitude),
                    longitude: String(data.longitude),
                    maxRadiusKm: String(data.maxRadiusKm),
                });
            } catch (error) {
                console.log(error);
            }
        };
        fetchProfileLocation();
    }, []);

    const handleChange = (field: keyof ProfileLocationForm, value: string) => {
        setFormValues(prev =>
            prev ? { ...prev, [field]: value } : prev
        );
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!formValues) return;
        try {
            const payload: ProfileLocation = {
                id: formValues.id,
                latitude: Number(formValues.latitude),
                longitude: Number(formValues.longitude),
                maxRadiusKm: Number(formValues.maxRadiusKm),
                city: formValues.city,
            };
            await UpdateLocation(payload);
            setIsEditing(false);
        } catch (error) {
            console.error(error);
        }
    };

    const options: PositionOptions = {
        enableHighAccuracy: true,
        timeout: 30000,
        maximumAge: 0,
    };

    function success(pos: GeolocationPosition) {
        setIsLoadingGeoLocation(false);
        const crd = pos.coords;
        handleChange("latitude", crd.latitude.toString());
        handleChange("longitude", crd.longitude.toString());
    }

    function error(err: any) {
        setIsLoadingGeoLocation(false);
        console.warn(`ERROR(${err.code}): ${err.message}`);
    }

    const loadGeolocation = (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoadingGeoLocation(true);
        if (!navigator.geolocation) {
            console.error("Geolocation is not supported by this browser.");
            return;
        }
        navigator.geolocation.getCurrentPosition(success, error, options);
    };

    if (!formValues) return <MySpinner />;

    return (
        <Card className="mb-4">
            <Form onSubmit={handleSave}>
                <Card.Header className="d-flex justify-content-between align-items-center">
                    Geo Location
                    {isEditing ? (
                        <>
                            <Button
                                type="button"
                                variant="outline-primary"
                                onClick={loadGeolocation}
                                disabled={isLoadingGeoLocation}
                            >
                                Load Geolocation
                            </Button>
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
                    <Form.Group className="small mb-1" controlId="formCity">
                        <Form.Label>City</Form.Label>
                        <Form.Select aria-label="Default select example" disabled={!isEditing} value={formValues.city || ""} onChange={(e) => {
                            const selectedCity = cityList.find(
                                (c) => c.name === e.target.value
                            );
                            if (!selectedCity) return;
                            handleChange("city", selectedCity.name.toString());
                            handleChange("latitude", selectedCity.latitude.toString());
                            handleChange("longitude", selectedCity.longitude.toString());
                        }}>
                            <option value="">Select City</option>
                            {cityList.map(city => (
                                <option key={city.name} value={city.name}>
                                    {city.name}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="small mb-1" controlId="formMaxRadiusKm">
                        <Form.Label>Max Radius Km</Form.Label>
                        <Form.Control
                            type="number"
                            placeholder="Enter Max Radius Km"
                            value={formValues?.maxRadiusKm ?? ""}
                            disabled={!isEditing}
                            required={isEditing}
                            onChange={e => handleChange("maxRadiusKm", e.target.value)}
                        />
                    </Form.Group>
                    {isLoadingGeoLocation &&
                        <MySpinner />
                    }
                    {!isLoadingGeoLocation &&
                        <>
                            <Form.Group className="small mb-1" controlId="formLatitude">
                                <Form.Label>Latitude</Form.Label>
                                <Form.Control
                                    type="number"
                                    placeholder="Enter Latitude"
                                    value={formValues?.latitude ?? ""}
                                    disabled={!isEditing}
                                    required={isEditing}
                                    onChange={e => handleChange("latitude", e.target.value)}
                                />
                            </Form.Group>
                            <Form.Group className="small mb-1" controlId="formLongitude">
                                <Form.Label>Longitude</Form.Label>
                                <Form.Control
                                    type="number"
                                    placeholder="Enter Longitude"
                                    value={formValues?.longitude ?? ""}
                                    disabled={!isEditing}
                                    required={isEditing}
                                    onChange={e => handleChange("longitude", e.target.value)}
                                />
                            </Form.Group>
                        </>
                    }
                </Card.Body>
            </Form>
        </Card>
    );
}