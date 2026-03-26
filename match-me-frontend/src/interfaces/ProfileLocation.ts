export interface ProfileLocation {
  id: number;
  latitude: number;
  longitude: number;
  maxRadiusKm: number;
  city: string;
}

export interface ProfileLocationForm {
  latitude: string;
  longitude: string;
  maxRadiusKm: string;
  city: string;
  id: number;
}